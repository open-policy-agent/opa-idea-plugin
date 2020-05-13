/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin.ide.highlight

import com.github.vgramer.opaplugin.ide.colors.RegoColor.*
import com.github.vgramer.opaplugin.lang.lexer.RegoLexerAdapter
import com.github.vgramer.opaplugin.lang.psi.REGO_KEYWORDS
import com.github.vgramer.opaplugin.lang.psi.REGO_OPERATOR
import com.github.vgramer.opaplugin.lang.psi.RegoTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

/**
 * Handle the Syntax Highlighting of the a file.
 *
 * Color can be customize be the user thank the [com.github.vgramer.opaplugin.ide.colors.RegoColorSettingsPage]
 * If you want let the user customize a color, you must update the [com.github.vgramer.opaplugin.ide.colors.RegoColor]
 */
class RegoHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = RegoLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = pack(tokenToColorMap[tokenType])

    val tokenToColorMap: Map<IElementType, TextAttributesKey> = HashMap()

    init {
        fillMap(tokenToColorMap, LINE_COMMENT.textAttributesKey, RegoTypes.COMMENT)
        // TODO Handle raw String ?
        fillMap(tokenToColorMap, STRING.textAttributesKey, RegoTypes.STRING_TOKEN)

        fillMap(tokenToColorMap, BRACES.textAttributesKey, RegoTypes.RBRACE)
        fillMap(tokenToColorMap, BRACES.textAttributesKey, RegoTypes.LBRACE)

        fillMap(tokenToColorMap, BRACKETS.textAttributesKey, RegoTypes.RBRACK)
        fillMap(tokenToColorMap, BRACKETS.textAttributesKey, RegoTypes.LBRACK)

        fillMap(tokenToColorMap, PARENTHESES.textAttributesKey, RegoTypes.RPAREN)
        fillMap(tokenToColorMap, PARENTHESES.textAttributesKey, RegoTypes.LPAREN)

        fillMap(tokenToColorMap, COMMA.textAttributesKey, RegoTypes.COMMA)
        fillMap(tokenToColorMap, SEMICOLON.textAttributesKey, RegoTypes.SEMICOLON)
        fillMap(tokenToColorMap, DOT.textAttributesKey, RegoTypes.DOT)

        fillMap(tokenToColorMap, NUMBER.textAttributesKey, RegoTypes.NUMBER)
        fillMap(tokenToColorMap, BOOLEAN.textAttributesKey, RegoTypes.TRUE)
        fillMap(tokenToColorMap, BOOLEAN.textAttributesKey, RegoTypes.FALSE)

        fillMap(tokenToColorMap, REGO_OPERATOR, OPERATOR.textAttributesKey)
        fillMap(tokenToColorMap, REGO_KEYWORDS, KEYWORD.textAttributesKey)

    }

}

