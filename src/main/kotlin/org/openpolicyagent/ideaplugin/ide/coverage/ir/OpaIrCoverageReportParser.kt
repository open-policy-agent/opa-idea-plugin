/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package org.openpolicyagent.ideaplugin.ide.coverage.ir

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.openapi.project.Project
import com.intellij.rt.coverage.data.LineCoverage
import com.intellij.rt.coverage.data.LineData
import com.intellij.rt.coverage.data.ProjectData
import org.openpolicyagent.ideaplugin.opa.project.settings.OpaProjectSettings
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Parses the JSON written by the SDK's coverage profiler into IntelliJ's [ProjectData].
 *
 * The wire format is the SDK's `OpaCoverageReport` (see `opa-jackson`'s `OpaCoverageReport.from`):
 * ```
 * { "files": { "<build-time-path>": {
 *     "covered": [ { "start": {"row": r, "col": c}, "end": {...} }, ... ],
 *     "not_covered": [ { "start": {...}, "end": {...} }, ... ]
 * } } }
 * ```
 *
 * Paths are absolute and fixed at `opa build` time. Even when the bundle was built here they may
 * not match this project's layout, and the bundle can also come from another machine. So we point
 * them at the local Rego source root instead.
 *
 * `not_covered` means the plan's `unplanned_rules`, rules the planner never compiled. It does not
 * mean dead statements inside rules that were compiled. The SDK has no way to check that.
 *
 * Each forked test JVM writes its own report. We combine all rows from all reports, and a row
 * counts as covered if any report says so. We only decide this once every report has been read,
 * so the order we read them in does not matter.
 */
class OpaIrCoverageReportParser {

    fun parse(jsonFiles: List<File>, project: Project): ProjectData {
        val sourceRoot = OpaProjectSettings.getInstance(project).regoBundleSourceRoot
        val projectBase = project.basePath ?: return ProjectData()
        val localSourceRoot = Paths.get(projectBase, sourceRoot)
        return parseAll(jsonFiles.map { it.readText() }, sourceRoot, localSourceRoot)
    }

    /** Overload for unit tests, no [Project] needed. */
    internal fun parse(jsonText: String, sourceRootName: String, localSourceRoot: Path): ProjectData =
        parseAll(listOf(jsonText), sourceRootName, localSourceRoot)

    internal fun parseAll(
        jsonTexts: List<String>,
        sourceRootName: String,
        localSourceRoot: Path,
    ): ProjectData {
        val data = ProjectData()
        val coveredByFile = linkedMapOf<String, MutableSet<Int>>()
        val notCoveredByFile = linkedMapOf<String, MutableSet<Int>>()

        for (jsonText in jsonTexts) {
            val root = JsonParser.parseString(jsonText).asJsonObject
            val files = if (root.has("files")) root.getAsJsonObject("files") else continue
            for ((buildPath, entry) in files.entrySet()) {
                val localPath = remapPath(buildPath, sourceRootName, localSourceRoot) ?: continue
                val fileObj = entry.asJsonObject
                coveredByFile.getOrPut(localPath) { mutableSetOf() }.addAll(fileObj.rows("covered"))
                notCoveredByFile.getOrPut(localPath) { mutableSetOf() }
                    .addAll(fileObj.rows("not_covered"))
            }
        }

        for (localPath in coveredByFile.keys) {
            val covered = coveredByFile.getValue(localPath)
            // A row is covered if it ran in even one JVM, even if another JVM says the row is unplanned.
            val notCovered = notCoveredByFile.getValue(localPath) - covered
            if (covered.isEmpty() && notCovered.isEmpty()) continue

            val maxRow = maxOf(covered.maxOrNull() ?: 0, notCovered.maxOrNull() ?: 0)

            // SimpleCoverageAnnotator.normalizeFilePath lowercases paths on case-insensitive
            // filesystems (macOS). So we store the path both as-is (used for gutter lookup via
            // getQualifiedNames) and lowercase (used for the coverage tree via
            // collectBaseFileCoverage).
            val keys = linkedSetOf(localPath)
            val lowercasedPath = localPath.lowercase()
            if (lowercasedPath != localPath) keys.add(lowercasedPath)
            for (key in keys) {
                val lineArr = arrayOfNulls<LineData>(maxRow + 1)
                for (row in notCovered) {
                    lineArr[row] = LineData(row, "").apply { setStatus(LineCoverage.NONE) }
                }
                for (row in covered) {
                    lineArr[row] = LineData(row, "").apply { setStatus(LineCoverage.FULL) }
                }
                data.getOrCreateClassData(key).setLines(lineArr)
            }
        }
        return data
    }

    /**
     * Reanchors a build-time path under [localSourceRoot], keyed off the last path segment matching
     * the source root. So with source root `rego`, `/build/agent/rego/policy.rego` becomes
     * `<localSourceRoot>/policy.rego`. Returns null if no segment matches. The result is
     * canonicalized to match the canonicalized `VirtualFile` the platform hands [OpaIrCoverageAnnotator],
     * which differs from a plain resolved path when the project path goes through a symlink.
     */
    internal fun remapPath(
        buildPath: String,
        sourceRootName: String,
        localSourceRoot: Path,
    ): String? {
        val segments = buildPath.split('/', '\\').filter { it.isNotEmpty() }
        val marker = sourceRootName.split('/', '\\').last { it.isNotEmpty() }
        val lastIdx = segments.indexOfLast { it == marker }
        if (lastIdx < 0 || lastIdx == segments.lastIndex) return null
        val relative = segments.drop(lastIdx + 1).joinToString("/")
        return canonicalize(localSourceRoot.resolve(relative).toString())
    }

    /**
     * Turns `{start:{row},end:{row}}` ranges into the rows they cover. `col` is dropped because
     * IntelliJ's [LineData] only works at the line level. Rows of 0 or less are dropped too: some
     * SDK versions give synthetic statements no real source location.
     */
    private fun JsonObject.rows(name: String): Set<Int> {
        if (!has(name)) return emptySet()
        val rows = mutableSetOf<Int>()
        for (element in getAsJsonArray(name)) {
            val range = element.asJsonObject
            val startRow = range.getAsJsonObject("start").get("row").asInt
            val endRow = range.getAsJsonObject("end").get("row").asInt
            for (row in startRow..endRow) {
                if (row > 0) rows.add(row)
            }
        }
        return rows
    }
}

/**
 * `File.canonicalPath`, but falls back to [path] unchanged if that fails (for example, a
 * permissions error while checking a parent directory). Safe to call on a path that does not
 * exist, since canonicalization only resolves the parts that do.
 */
internal fun canonicalize(path: String): String =
    try {
        File(path).canonicalPath
    } catch (e: IOException) {
        path
    }
