/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.opa.ideaplugin.ide.colors.RegoColor.*
import org.opa.ideaplugin.lang.lexer.RegoLexerAdapter
import org.opa.ideaplugin.lang.psi.REGO_KEYWORDS
import org.opa.ideaplugin.lang.psi.REGO_OPERATOR
import org.opa.ideaplugin.lang.psi.RegoTypes

/**
 * Handle the Syntax Highlighting of the a file.
 *
 * Color can be customize be the user thank the [org.opa.ideaplugin.ide.colors.RegoColorSettingsPage]
 * If you want let the user customize a color, you must update the [org.opa.ideaplugin.ide.colors.RegoColor]
 */
class RegoHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = RegoLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = pack(tokenToColorMap[tokenType])

    val tokenToColorMap: Map<IElementType, TextAttributesKey> = HashMap()

    init {
        fillMap(tokenToColorMap, LINE_COMMENT.textAttributesKey, RegoTypes.COMMENT)
        // TODO Handle raw String ?
        fillMap(tokenToColorMap, STRING.textAttributesKey, RegoTypes.STRING_LITERAL)

        fillMap(tokenToColorMap, BRACES.textAttributesKey, RegoTypes.RBRACE)
        fillMap(tokenToColorMap, BRACES.textAttributesKey, RegoTypes.LBRACE)

        fillMap(tokenToColorMap, BRACKETS.textAttributesKey, RegoTypes.RBRACK)
        fillMap(tokenToColorMap, BRACKETS.textAttributesKey, RegoTypes.LBRACK)

        fillMap(tokenToColorMap, PARENTHESES.textAttributesKey, RegoTypes.RPAREN)
        fillMap(tokenToColorMap, PARENTHESES.textAttributesKey, RegoTypes.LPAREN)

        fillMap(tokenToColorMap, COMMA.textAttributesKey, RegoTypes.COMMA)
        fillMap(tokenToColorMap, SEMICOLON.textAttributesKey, RegoTypes.SEMIC)
        fillMap(tokenToColorMap, DOT.textAttributesKey, RegoTypes.DOT)

        fillMap(tokenToColorMap, NUMBER.textAttributesKey, RegoTypes.NUMBER)
        fillMap(tokenToColorMap, BOOLEAN.textAttributesKey, RegoTypes.TRUE_KW)
        fillMap(tokenToColorMap, BOOLEAN.textAttributesKey, RegoTypes.FALSE_KW)

        fillMap(tokenToColorMap, REGO_OPERATOR, OPERATOR.textAttributesKey)
        fillMap(tokenToColorMap, REGO_KEYWORDS, KEYWORD.textAttributesKey)

    }

}

