/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package org.openpolicyagent.ideaplugin.ide.coverage.ir

import com.intellij.coverage.BaseCoverageAnnotator
import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageSuitesBundle
import com.intellij.coverage.SimpleCoverageAnnotator
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.rt.coverage.data.ProjectData

/** Coverage annotator for `.rego` files: gutter/tree display for the Rego IR coverage engine. */
@Service(Service.Level.PROJECT)
class OpaIrCoverageAnnotator(project: Project) : SimpleCoverageAnnotator(project) {

    /**
     * Reads coverage for [vFile] straight from [ProjectData], so it does not wait for the platform's
     * cache to fill in later. [ProjectData] uses canonical paths as keys (see
     * [OpaIrCoverageReportParser.remapPath]). The platform canonicalizes the file for the Coverage
     * tool window, but not always for other callers, so we canonicalize here too. This keeps every
     * caller using the same key. We check the lowercase path first, for case-insensitive filesystems.
     */
    private fun getDirectCoverageInfoString(
        vFile: VirtualFile,
        bundle: CoverageSuitesBundle,
    ): String? {
        val projectData = bundle.getCoverageData() ?: return null
        val path = canonicalize(vFile.path)
        val classData = projectData.getClassData(path.lowercase())
            ?: projectData.getClassData(path)
            ?: return null
        val info = fileInfoForCoveredFile(classData) ?: return null
        return getLinesCoverageInformationString(info)
    }

    override fun getFileCoverageInformationString(
        project: Project,
        vFile: VirtualFile,
        bundle: CoverageSuitesBundle,
        manager: CoverageDataManager,
    ): String? {
        if (vFile.name.endsWith(".rego")) {
            val direct = getDirectCoverageInfoString(vFile, bundle)
            if (direct != null) return direct
        }

        return super.getFileCoverageInformationString(project, vFile, bundle, manager)
    }

    // Same idea for directories. Without this, rego/ only shows up after the platform's
    // background scan fills in myDirCoverageInfos.
    override fun getDirCoverageInformationString(
        project: Project,
        vFile: VirtualFile,
        bundle: CoverageSuitesBundle,
        manager: CoverageDataManager,
    ): String? {
        val projectData = bundle.getCoverageData()
            ?: return super.getDirCoverageInformationString(project, vFile, bundle, manager)

        // We canonicalize here for the same reason as above: ProjectData keys are canonical
        // paths, but the directory we get here is not canonicalized.
        val dirPrefix = canonicalize(vFile.path).lowercase() + "/"
        val regoEntries = projectData.classes.keys
            .filter { it.endsWith(".rego") && it.startsWith(dirPrefix) }
        if (regoEntries.isEmpty()) {
            return super.getDirCoverageInformationString(project, vFile, bundle, manager)
        }

        val info = BaseCoverageAnnotator.DirCoverageInfo()
        info.totalFilesCount = regoEntries.size
        for (key in regoEntries) {
            val classData = projectData.getClassData(key) ?: continue
            val fileInfo = fileInfoForCoveredFile(classData)
            if (fileInfo != null && fileInfo.coveredLineCount > 0) info.coveredFilesCount++
            info.totalLineCount += fileInfo?.totalLineCount ?: 0
            info.coveredLineCount += fileInfo?.coveredLineCount ?: 0
        }
        return getFilesCoverageInformationString(info)
            ?: super.getDirCoverageInformationString(project, vFile, bundle, manager)
    }

    override fun annotate(
        dir: VirtualFile,
        bundle: CoverageSuitesBundle,
        manager: CoverageDataManager,
        projectData: ProjectData,
        project: Project,
        runner: CoverageAnnotatorRunner,
    ) {
        super.annotate(dir, bundle, manager, projectData, project, runner)

        val regoKeys = projectData.classes.keys.filter { it.endsWith(".rego") }
        if (regoKeys.isEmpty()) return

        val regoDirs = regoKeys.mapNotNull { key ->
            LocalFileSystem.getInstance().findFileByPath(key)?.parent
        }.toSet()

        val projectFileIndex = ProjectFileIndex.getInstance(project)
        for (regoDir in regoDirs) {
            // The base annotate() only checks the project's registered source folders. A plain
            // rego/ folder is usually not one of these. Without this step, its coverage is missing.
            collectFolderCoverage(
                regoDir,
                manager,
                runner,
                projectData,
                false,
                projectFileIndex,
                bundle.coverageEngine,
                mutableSetOf(), // visitedDirs: new set each call, since each rego dir is walked once from its own parent
                emptyMap(), // normalizedFiles2Files: not needed, the parser stores both case keys
            )
        }
    }
}
