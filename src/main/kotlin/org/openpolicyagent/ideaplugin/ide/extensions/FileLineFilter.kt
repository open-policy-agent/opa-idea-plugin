/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.extensions


import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfoFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.util.regex.Pattern

/**
 * Filter to attach to a console which will hyperlink any filename:row:col
 * logging message to the actual file/row specified
 *
 * based off of https://github.com/anthraxx/intellij-awesome-console/blob/master/src/awesome/console/AwesomeLinkFilter.java
 */

class FileLineFilter(project: Project): Filter {
    val project = project

    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        val basepath = project.basePath ?: return null //we will need to know the project basepath to hyperlink
        val startpoint = entireLength - line.length
        //regex from cited source...any simplifications welcome
        val matcher = Pattern.compile(
                "(?<link>(?<path>([.~])?(?:[a-zA-Z]:\\\\|/)?\\w[\\w/\\-.\\\\]*\\.[\\w\\-.]+)\\$?" +
                        "(?:(?::|, line |\\()(?<row>\\d+)(?:[:,]( column )?(?<col>\\d+)\\)?)?)?)",
                Pattern.UNICODE_CHARACTER_CLASS).matcher(line)
        val results = mutableListOf<Filter.ResultItem>()
        while (matcher.find()) {

            var path = matcher.group("path")

            //if no row is supplied, do not hyperlink (largely due to regex matching collisions with
            //rego values like data.xyz or input.xyz)
            val row = matcher.group("row")?.toInt() ?: continue

            val files = mutableListOf<VirtualFile>()
            //path could be relative to project basedir path or absolute path
            if (path.removePrefix(basepath) == path) {
                path = "$basepath/$path"
            }
            val file = LocalFileSystem.getInstance().findFileByPath(path)
            if (file != null) {
                files.add(file)
            }


            val hyperlink = HyperlinkInfoFactory.getInstance().createMultipleFilesHyperlinkInfo(files, row - 1, project)
            results.add(Filter.ResultItem(startpoint + matcher.start(), startpoint+matcher.end(), hyperlink))
        }
        return Filter.Result(results)
    }
}
