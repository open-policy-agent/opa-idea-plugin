/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.io.StreamUtil
import org.openpolicyagent.ideaplugin.ide.highlight.RegoHighlighter
import org.openpolicyagent.ideaplugin.lang.RegoIcons
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.swing.Icon

/**
 * Settings page where user can customize the highlighting for the Rego language.
 */
class RegoColorSettingsPage : ColorSettingsPage {
    private val attributes = RegoColor.values().map { it.attributesDescriptor }.toTypedArray()
    private val tags = RegoColor.values().associateBy({ it.name }, { it.textAttributesKey })


    /**
     * Demo text shown in the preview pane.
     */
    private val regoDemoText by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("org/openpolicyagent/ideaplugin/ide/colors/RegoDemo.rego")
        StreamUtil.convertSeparators(StreamUtil.readText(InputStreamReader(stream, StandardCharsets.UTF_8)))
    }

    override fun getDisplayName(): String = "Rego"
    override fun getIcon(): Icon = RegoIcons.OPA
    override fun getHighlighter(): SyntaxHighlighter = RegoHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = tags
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = attributes
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getDemoText(): String = regoDemoText
}