package com.github.vgramer.opaplugin.ide.colors

import com.github.vgramer.opaplugin.ide.highlight.RegoHighlighter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RegoColorSettingsPageTest {

    @Test
    fun `demo text should be in the classpath and not be empty`() {
        val page = RegoColorSettingsPage()
        assertThat(page.demoText).isNotEmpty()
    }

    @Test
    fun `token defined in the color settings page should be defined in the highlighter`() {
        val colorSettingsAttributes = RegoColorSettingsPage().attributeDescriptors.map { it.key }

        assertThat(colorSettingsAttributes)
            .describedAs("Some token are defined in the color settings page but not in Highlighter. That mean user can choose color for this token but ide will never render it")
            .containsExactlyInAnyOrderElementsOf(RegoHighlighter().tokenToColorMap.values.distinct())


    }
}