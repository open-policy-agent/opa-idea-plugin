/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.todo

import com.intellij.lexer.Lexer
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.search.IndexPatternBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.openpolicyagent.ideaplugin.lang.lexer.RegoLexerAdapter
import org.openpolicyagent.ideaplugin.lang.psi.REGO_COMMENT
import org.openpolicyagent.ideaplugin.lang.psi.RegoFile

class RegoTodoIndexPatternBuilder : IndexPatternBuilder {

    override fun getIndexingLexer(file: PsiFile): Lexer? = if (file is RegoFile) RegoLexerAdapter() else null

    /**
     * set of token in which PatternBuilder can be applied
     */
    override fun getCommentTokenSet(file: PsiFile): TokenSet? = if (file is RegoFile) REGO_COMMENT else null

    /**
     * offset from start to delimit the pattern matched. It use to avoid to index the opening comment (ie '#'')
     */
    override fun getCommentStartDelta(tokenType: IElementType?): Int = if (tokenType in REGO_COMMENT) 1 else 0

    /**
     * offset from end to delimit the pattern matched. It use to avoid to index the closing comment character(s) in some
     * language. If it set set to 0, that mean it will match line till end.
     */
    override fun getCommentEndDelta(tokenType: IElementType?): Int = 0
}