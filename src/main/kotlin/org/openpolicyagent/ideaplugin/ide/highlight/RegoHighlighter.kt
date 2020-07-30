/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.openpolicyagent.ideaplugin.ide.colors.RegoColor.*
import org.openpolicyagent.ideaplugin.lang.lexer.RegoLexerAdapter
import org.openpolicyagent.ideaplugin.lang.psi.REGO_KEYWORDS
import org.openpolicyagent.ideaplugin.lang.psi.REGO_OPERATOR
import org.openpolicyagent.ideaplugin.lang.psi.RegoTypes

/**
 * Handle the Syntax Highlighting of a file.
 *
 * Color can be customized by the user with the [org.openpolicyagent.ideaplugin.ide.colors.RegoColorSettingsPage]
 * If you wish to let the user customize a color, update [org.openpolicyagent.ideaplugin.ide.colors.RegoColor]
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

