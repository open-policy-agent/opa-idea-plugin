/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package org.openpolicyagent.ideaplugin.ide.coverage.ir

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageEditorAnnotatorImpl
import com.intellij.coverage.CoverageEngine
import com.intellij.coverage.CoverageExecutor
import com.intellij.coverage.CoverageRunner
import com.intellij.coverage.DefaultCoverageFileProvider
import com.intellij.execution.ExecutionListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskType
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.util.concurrency.AppExecutorUtil
import org.gradle.util.GradleVersion
import org.jetbrains.plugins.gradle.service.task.GradleTaskManagerExtension
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings
import org.jetbrains.plugins.gradle.util.GradleConstants
import org.openpolicyagent.ideaplugin.lang.psi.RegoFile
import org.openpolicyagent.ideaplugin.opa.project.settings.OpaProjectSettings
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

/*
 * One coverage run, start to finish. The platform splits this work across four extension points,
 * so each step below is its own class. They fire in this order:
 *
 *   1. OpaIrCoverageExecutionListener   prepares a run when Gradle is launched with the Coverage
 *                                       executor
 *   2. OpaIrCoverageGradleTaskManager   claims that run and injects the opa.coverage.output system
 *                                       property into the test JVMs
 *   3. OpaIrCoverageTaskNotificationListener  loads the reports once the build ends
 *
 * OpaIrCoverageGradleSession holds the state shared by all three, and makes sure an unrelated
 * Gradle run cannot steal this one.
 */

/**
 * A run that has been prepared for IR coverage collection.
 *
 * [outputDir] holds one report per JVM: the shared init script gives every Test task and fork the
 * same directory, and each writes its own `report-<runId>-<index>.json` inside it (the SDK's
 * coverage profiler owns that naming).
 *
 * [taskId] is the `ExternalSystemTaskId` of the Gradle build run this coverage run is bound to. It
 * is set later, once Gradle configures the build. See [OpaIrCoverageGradleSession.bindTaskId].
 */
data class OpaIrPendingRun(
    val outputDir: Path,
    val runConfigName: String,
    val startedAt: Long,
    val taskId: ExternalSystemTaskId? = null,
)

@Service(Service.Level.PROJECT)
class OpaIrCoverageGradleSession {

    @Volatile
    private var pendingRun: OpaIrPendingRun? = null

    fun prepare(runConfigName: String): OpaIrPendingRun {
        val parent = Path.of(System.getProperty("java.io.tmpdir"), REPORT_ROOT_DIR_NAME)
        Files.createDirectories(parent)

        clearStaleRuns(parent)

        val run = OpaIrPendingRun(
            outputDir = Files.createTempDirectory(parent, "run-"),
            runConfigName = runConfigName,
            startedAt = System.currentTimeMillis(),
        )
        pendingRun = run
        return run
    }

    /**
     * Links the prepared run to the Gradle task that will carry it, and says whether [id] is the
     * one that owns the run.
     *
     * The first caller wins, and we never change it after that. A run stays pending for the whole
     * build, so unrelated Gradle runs also reach configureTasks. If we let them change the link,
     * the wrong run's onEnd can grab the coverage. The real run then gets nothing.
     */
    fun bindTaskId(id: ExternalSystemTaskId): Boolean = synchronized(this) {
        val run = pendingRun ?: return false
        val bound = run.taskId
        if (bound != null) return bound == id
        pendingRun = run.copy(taskId = id)
        return true
    }

    fun lookup(): OpaIrPendingRun? = pendingRun

    /**
     * Takes the pending run, but only if it belongs to [id].
     *
     * If a run is prepared but never configured (build fails early, or is cancelled), it just
     * stays pending until the next prepare() replaces it. We do not take unbound runs when another
     * task ends. A sync running at the same time can then steal the coverage.
     */
    fun consumeFor(id: ExternalSystemTaskId): OpaIrPendingRun? = synchronized(this) {
        val run = pendingRun ?: return null
        if (run.taskId != id) return null
        pendingRun = null
        return run
    }

    private fun clearStaleRuns(parent: Path) {
        val cutoff = System.currentTimeMillis() - STALE_AFTER_MS
        val children = parent.toFile().listFiles() ?: return
        for (child in children) {
            try {
                if (!child.isDirectory || child.lastModified() >= cutoff) continue
                child.deleteRecursively()
            } catch (e: Exception) {
                thisLogger().debug("OPA IR coverage: could not clear stale run dir $child", e)
            }
        }
    }

    companion object {
        private const val REPORT_ROOT_DIR_NAME = "opa-ir-coverage"
        private val STALE_AFTER_MS = TimeUnit.DAYS.toMillis(1)

        /** Matches only successfully written reports: skips in-progress `.tmp` files and zero-length ones. */
        fun isReportFile(f: File): Boolean =
            f.isFile && f.name.startsWith("report-") && f.name.endsWith(".json") && f.length() > 0

        /** Reports for a run, sorted by name so that loading (and its logging) is deterministic. */
        fun reportsIn(dir: File): List<File> =
            dir.listFiles { f: File -> isReportFile(f) }?.sortedBy { it.name }.orEmpty()
    }
}

class OpaIrCoverageExecutionListener(private val project: Project) : ExecutionListener {

    override fun processStartScheduled(executorId: String, env: ExecutionEnvironment) {
        if (executorId != CoverageExecutor.EXECUTOR_ID) return
        val runProfile = env.runProfile as? ExternalSystemRunConfiguration ?: return
        if (runProfile.settings.externalSystemId != GradleConstants.SYSTEM_ID) return

        if (!hasRegoSourceRoot(project)) {
            thisLogger().debug("OPA IR coverage: not preparing, no Rego source root found in project")
            return
        }

        val session = project.getService(OpaIrCoverageGradleSession::class.java)
        try {
            session.prepare(runProfile.name)
        } catch (e: Exception) {
            // prepare() touches the filesystem in a message-bus callback. A thrown IOException
            // surfaces as an internal error on every "Run with coverage".
            thisLogger().warn("OPA IR coverage: could not prepare run; coverage will not be collected", e)
        }
    }

    // Keeps plain Java/Gradle projects with no Rego bundle untouched: no temp dir, no init script.
    private fun hasRegoSourceRoot(project: Project): Boolean {
        val basePath = project.basePath ?: return false
        val sourceRoot = OpaProjectSettings.getInstance(project).regoBundleSourceRoot
        return File(basePath, sourceRoot).isDirectory
    }
}

class OpaIrCoverageGradleTaskManager : GradleTaskManagerExtension {

    override fun configureTasks(
        projectPath: String,
        id: ExternalSystemTaskId,
        settings: GradleExecutionSettings,
        gradleVersion: GradleVersion?,
    ) {
        val project = id.findProject() ?: return
        val session = project.getService(OpaIrCoverageGradleSession::class.java)

        // Project syncs (RESOLVE_PROJECT) also reach configureTasks. They do not run any Test
        // tasks, so they must not take ownership of the prepared run.
        if (id.type != ExternalSystemTaskType.EXECUTE_TASK) return

        if (!session.bindTaskId(id)) {
            thisLogger().debug(
                "OPA IR coverage: run already bound to another Gradle invocation; not instrumenting $projectPath")
            return
        }

        val pending = session.lookup() ?: return

        val outputPath = pending.outputDir.toString().replace("\\", "\\\\").replace("'", "\\'")

        val initScript = """
            allprojects {
                tasks.withType(org.gradle.api.tasks.testing.Test).configureEach {
                    systemProperty 'opa.coverage.output', '$outputPath'
                }
            }
        """.trimIndent()

        settings.addInitScript("opa-ir-coverage", initScript)
        thisLogger().info("Injected OPA IR coverage init script for $projectPath")
    }
}

class OpaIrCoverageTaskNotificationListener : ExternalSystemTaskNotificationListener {

    override fun onEnd(projectPath: String, id: ExternalSystemTaskId) {
        if (id.projectSystemId != GradleConstants.SYSTEM_ID) return

        val project = id.findProject() ?: return
        val session = project.getService(OpaIrCoverageGradleSession::class.java)
        // Only the task that claimed the run may consume it, so another task ending in between
        // cannot read the reports before the test JVMs finish writing them.
        val pending = session.consumeFor(id) ?: return

        val reports = OpaIrCoverageGradleSession.reportsIn(pending.outputDir.toFile())
        if (reports.isEmpty()) {
            thisLogger().info(
                "OPA IR coverage: no reports in ${pending.outputDir}; the most likely cause is that " +
                    "opa-jackson is not on the tested application's runtime classpath, so no profiler " +
                    "ever wrote a report"
            )
            return
        }

        ApplicationManager.getApplication().invokeLater({
            val runner = CoverageRunner.getInstance(OpaIrCoverageRunner::class.java) ?: run {
                thisLogger().warn("OPA IR coverage: OpaIrCoverageRunner not found")
                return@invokeLater
            }
            val engine = CoverageEngine.EP_NAME.findExtension(OpaIrCoverageEngine::class.java) ?: run {
                thisLogger().warn("OPA IR coverage: OpaIrCoverageEngine not found")
                return@invokeLater
            }
            val provider = DefaultCoverageFileProvider(pending.outputDir.toFile())
            val suite = engine.createCoverageSuite(
                "Rego IR — ${pending.runConfigName}",
                project,
                runner,
                provider,
                pending.startedAt,
            )
            val manager = CoverageDataManager.getInstance(project)
            manager.coverageGathered(suite)
            thisLogger().info(
                "OPA IR coverage: loaded suite from ${pending.outputDir} (${reports.size} report(s))"
            )

            annotateOpenRegoFiles(project, manager)
        }, ModalityState.nonModal())
    }

    /**
     * Drives [CoverageEditorAnnotatorImpl] over every open `.rego` editor.
     *
     * CoverageDataAnnotationsManager.show() goes through active bundles in ConcurrentHashMap order,
     * and stops as soon as one bundle's engine says it does not handle this file type. So with a Java
     * suite active, rego files never get gutter marks. We drive the annotator ourselves to work
     * around this.
     */
    private fun annotateOpenRegoFiles(project: Project, manager: CoverageDataManager) {
        val bundle = manager.activeSuites().find { it.coverageEngine is OpaIrCoverageEngine }
            ?: return
        val psiManager = PsiManager.getInstance(project)
        val toAnnotate = mutableListOf<Pair<RegoFile, Editor>>()
        for (vFile in FileEditorManager.getInstance(project).openFiles) {
            if (!vFile.name.endsWith(".rego")) continue
            val psiFile = psiManager.findFile(vFile) as? RegoFile ?: continue
            for (fileEditor in FileEditorManager.getInstance(project).getAllEditors(vFile)) {
                if (fileEditor !is TextEditor) continue
                toAnnotate.add(psiFile to fileEditor.editor)
            }
        }
        ReadAction.nonBlocking(Runnable {
            for ((psiFile, editor) in toAnnotate) {
                if (!psiFile.isValid || editor.isDisposed) continue
                CoverageEditorAnnotatorImpl(psiFile, editor).showCoverage(bundle)
            }
        })
            .expireWith(project)
            .submit(AppExecutorUtil.getAppExecutorService())
    }
}
