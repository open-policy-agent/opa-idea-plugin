package com.github.vgramer.opaplugin.ide.highlight

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter

class RegoHighlighterFactory: SingleLazyInstanceSyntaxHighlighterFactory() {
    override fun createHighlighter(): SyntaxHighlighter = RegoHighlighter()
}