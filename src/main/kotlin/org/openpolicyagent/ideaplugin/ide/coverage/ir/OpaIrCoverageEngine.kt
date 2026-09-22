/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package org.openpolicyagent.ideaplugin.ide.coverage.ir

import com.intellij.coverage.BaseCoverageSuite
import com.intellij.coverage.CoverageAnnotator
import com.intellij.coverage.CoverageEngine
import com.intellij.coverage.CoverageFileProvider
import com.intellij.coverage.CoverageLoadErrorReporter
import com.intellij.coverage.CoverageLoadingResult
import com.intellij.coverage.CoverageRunner
import com.intellij.coverage.CoverageSuite
import com.intellij.coverage.CoverageSuitesBundle
import com.intellij.coverage.FailedCoverageLoadingResult
import com.intellij.coverage.SuccessCoverageLoadingResult
import com.intellij.coverage.view.CoverageViewExtension
import com.intellij.coverage.view.DirectoryCoverageViewExtension
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.openpolicyagent.ideaplugin.lang.psi.RegoFile
import java.io.File

/*
 * The platform's Coverage API needs three separate classes: CoverageEngine, CoverageSuite, and
 * CoverageRunner. Each must be its own class because the platform creates them by name, but they
 * are only ever used together here.
 */

class OpaIrCoverageEngine : CoverageEngine() {

    override fun getPresentableText() = "Rego IR Coverage"

    override fun isApplicableTo(config: RunConfigurationBase<*>) = false

    override fun createCoverageEnabledConfiguration(config: RunConfigurationBase<*>): CoverageEnabledConfiguration =
        throw UnsupportedOperationException("OpaIrCoverageEngine does not attach to run configurations")

    override fun createEmptyCoverageSuite(runner: CoverageRunner): CoverageSuite = OpaIrCoverageSuite()

    override fun createCoverageSuite(
        name: String,
        project: Project,
        runner: CoverageRunner,
        provider: CoverageFileProvider,
        timestamp: Long,
    ): CoverageSuite = OpaIrCoverageSuite(name, project, runner, provider, timestamp)

    override fun getCoverageAnnotator(project: Project): CoverageAnnotator =
        project.getService(OpaIrCoverageAnnotator::class.java)

    override fun createCoverageViewExtension(
        project: Project,
        bundle: CoverageSuitesBundle,
    ): CoverageViewExtension = DirectoryCoverageViewExtension(project, getCoverageAnnotator(project), bundle)

    override fun coverageEditorHighlightingApplicableTo(psiFile: PsiFile) = psiFile is RegoFile

    override fun acceptedByFilters(psiFile: PsiFile, bundle: CoverageSuitesBundle) = psiFile is RegoFile

    override fun getQualifiedNames(psiFile: PsiFile): Set<String> {
        val path = psiFile.virtualFile?.path ?: return emptySet()
        return setOf(path)
    }

    // Empty here makes the platform fall back to getQualifiedNames(PsiFile) for gutter lookup,
    // instead of getQualifiedName(File, PsiFile). That one defaults to null, which silently
    // turns off coverage display.
    override fun getCorrespondingOutputFiles(
        psiFile: PsiFile,
        module: Module?,
        bundle: CoverageSuitesBundle,
    ): Set<File> = emptySet()

    override fun recompileProjectAndRerunAction(
        module: Module,
        suite: CoverageSuitesBundle,
        chooseSuiteAction: Runnable,
    ) = false
}

class OpaIrCoverageSuite : BaseCoverageSuite {

    constructor() : super()

    constructor(
        name: String,
        project: Project,
        runner: CoverageRunner,
        provider: CoverageFileProvider,
        timestamp: Long,
    ) : super(name, project, runner, provider, timestamp)

    // getCoverageEngine() must not return null, and is called from places that cannot handle a
    // failure, so a missing extension means the build is broken. Fail loudly here instead of
    // letting first() throw its own confusing NoSuchElementException.
    override fun getCoverageEngine(): CoverageEngine =
        CoverageEngine.EP_NAME.findExtension(OpaIrCoverageEngine::class.java)
            ?: error("OpaIrCoverageEngine is not registered in the coverageEngine extension point")
}

class OpaIrCoverageRunner : CoverageRunner() {

    override fun getPresentableName() = "Rego IR Coverage"
    override fun getId() = "OpaIrCoverageRunner"

    // Only affects the platform's file chooser for manual suite import. Suites gathered by this
    // plugin point at a directory, which DefaultCoverageFileProvider.isValid() accepts.
    override fun getDataFileExtension() = "json"
    override fun acceptsCoverageEngine(engine: CoverageEngine) = engine is OpaIrCoverageEngine

    override fun loadCoverageData(
        sessionDataFile: File,
        baseCoverageSuite: CoverageSuite?,
        reporter: CoverageLoadErrorReporter,
    ): CoverageLoadingResult {
        val project = baseCoverageSuite?.getProject()
            ?: return FailedCoverageLoadingResult("No project associated with coverage suite")

        // The session data file is normally the run directory holding one report per test JVM. The
        // single-file branch keeps suites persisted by older plugin builds loadable.
        val reports: List<File> = if (sessionDataFile.isDirectory) {
            OpaIrCoverageGradleSession.reportsIn(sessionDataFile)
        } else {
            listOf(sessionDataFile)
        }
        if (reports.isEmpty()) {
            return FailedCoverageLoadingResult("No OPA IR coverage reports found in $sessionDataFile")
        }

        return try {
            SuccessCoverageLoadingResult(OpaIrCoverageReportParser().parse(reports, project))
        } catch (e: Exception) {
            FailedCoverageLoadingResult(e, false)
        }
    }
}
