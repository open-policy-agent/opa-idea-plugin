package com.github.vgramer.opaplugin.ide.colors

import com.github.vgramer.opaplugin.ide.highlight.RegoHighlighter
import com.github.vgramer.opaplugin.lang.RegoIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.io.StreamUtil
import javax.swing.Icon

/**
 * Settings page where user can customize the highlighting for the Rego language.
 */
class RegoColorSettingsPage : ColorSettingsPage {
    private val attributes = RegoColor.values().map { it.attributesDescriptor }.toTypedArray()
    private val tags = mutableMapOf<String, TextAttributesKey>()

    /**
     * Demo text shown in the preview pane.
     */
    private val regoDemoText by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("com/github/vgramer/opaplugin/ide/colors/RegoDemo.rego")
        StreamUtil.convertSeparators(StreamUtil.readText(stream, "UTF-8"))
    }

    override fun getDisplayName(): String = "Rego"
    override fun getIcon(): Icon = RegoIcons.OPA
    override fun getHighlighter(): SyntaxHighlighter = RegoHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey> = tags
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = attributes
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getDemoText(): String = regoDemoText
}