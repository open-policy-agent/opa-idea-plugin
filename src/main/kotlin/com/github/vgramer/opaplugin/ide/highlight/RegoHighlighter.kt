package com.github.vgramer.opaplugin.ide.highlight

import com.github.vgramer.opaplugin.ide.colors.RegoColor
import com.github.vgramer.opaplugin.lang.lexer.RegoLexerAdapter
import com.github.vgramer.opaplugin.lang.psi.REGO_KEYWORDS
import com.github.vgramer.opaplugin.lang.psi.REGO_OPERATOR
import com.github.vgramer.opaplugin.lang.psi.RegoTypes.*
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class RegoHighlighter : SyntaxHighlighter {
    override fun getHighlightingLexer(): Lexer = RegoLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        SyntaxHighlighterBase.pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType): RegoColor? = when (tokenType) {
            COMMENT -> RegoColor.LINE_COMMENT

            // TODO HANDLE RAW String
            STRING_TOKEN -> RegoColor.STRING

            RBRACE, LBRACE -> RegoColor.BRACES
            RPAREN, LPAREN -> RegoColor.PARENTHESES
            RBRACK, LBRACK -> RegoColor.BRACKETS

            COMMA -> RegoColor.COMMA
            SEMICOLON -> RegoColor.SEMICOLON

            in REGO_OPERATOR -> RegoColor.OPERATOR
            in REGO_KEYWORDS, TRUE, FALSE, NULL -> RegoColor.KEYWORD

            else -> null
        }
    }

}

