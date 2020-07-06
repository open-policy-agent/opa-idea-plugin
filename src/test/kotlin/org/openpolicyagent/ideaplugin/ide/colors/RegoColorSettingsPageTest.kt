/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.openpolicyagent.ideaplugin.ide.extensions.RegoHighlighterAnnotator
import org.openpolicyagent.ideaplugin.ide.highlight.RegoHighlighter

class RegoColorSettingsPageTest {

    @Test
    fun `demo text should be in the classpath and not be empty`() {
        val page = RegoColorSettingsPage()
        assertThat(page.demoText).isNotEmpty()
    }

    @Test
    fun `token defined in the color settings page should be defined in the highlighter`() {
        val colorSettingsAttributes = RegoColorSettingsPage().attributeDescriptors.map { it.key }
        val syntaxHighlighterColors : List<TextAttributesKey> = RegoHighlighter().tokenToColorMap.values.distinct()
        val highlighterAnnotatorColors = RegoHighlighterAnnotator().usedColors
        val allUsedColors : List<TextAttributesKey> = syntaxHighlighterColors + highlighterAnnotatorColors

        assertThat(colorSettingsAttributes)
            .describedAs("Some token are defined in the color settings page but not in Highlighter or Annotator. That mean user can choose color for this token but ide will never render it")
            .containsExactlyInAnyOrderElementsOf(allUsedColors)


    }
}