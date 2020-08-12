/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.linemarkers

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.intellij.lang.annotations.Language
import org.openpolicyagent.ideaplugin.FileTree
import org.openpolicyagent.ideaplugin.OpaTestBase

abstract class OpaLineMarkerProviderTestBase : OpaTestBase() {
    protected fun doTestByText(filename: String, @Language("rego") source: String) {
        myFixture.configureByText(filename, source)
        myFixture.doHighlighting()
        val expected = markersFrom(source)
        val actual = markersFrom(myFixture.editor, myFixture.project)
        assertEquals(expected.joinToString(COMPARE_SEPARATOR), actual.joinToString(COMPARE_SEPARATOR))
    }

    protected fun doTestFromFile(filePath: String, fileTree: FileTree) {
        fileTree.create()
        myFixture.configureFromTempProjectFile(filePath)
        myFixture.doHighlighting()
        val expected = markersFrom(myFixture.editor.document.text)
        val actual = markersFrom(myFixture.editor, myFixture.project)
        assertEquals(expected.joinToString(COMPARE_SEPARATOR), actual.joinToString(COMPARE_SEPARATOR))
    }

    private fun markersFrom(text: String) =
        text.split('\n')
            .withIndex()
            .filter { it.value.contains(MARKER) }
            .map { Pair(it.index, it.value.substring(it.value.indexOf(MARKER) + MARKER.length).trim()) }

    private fun markersFrom(editor: Editor, project: Project) =
        DaemonCodeAnalyzerImpl.getLineMarkers(editor.document, project)
            .map {
                Pair(editor.document.getLineNumber(it.element?.textRange?.startOffset as Int),
                    it.lineMarkerTooltip)
            }
            .sortedBy { it.first }

    private companion object {
        /**
         * Special comment that contains the tooltip of the expected marker
         */
        const val MARKER = "# - "

        /**
         * this separator is used when several marker  are present in the file
         */
        const val COMPARE_SEPARATOR = " | "
    }
}
