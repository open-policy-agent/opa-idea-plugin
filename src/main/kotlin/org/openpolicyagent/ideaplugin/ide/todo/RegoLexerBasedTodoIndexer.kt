/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.todo

import com.intellij.lexer.Lexer
import com.intellij.psi.impl.cache.impl.BaseFilterLexer
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer
import com.intellij.psi.search.UsageSearchContext
import org.openpolicyagent.ideaplugin.lang.lexer.RegoLexerAdapter
import org.openpolicyagent.ideaplugin.lang.psi.REGO_COMMENT

/**
 * Index Todos in Rego comments
 *
 * @see [org.openpolicyagent.ideaplugin.ide.todo.RegoTodoIndexPatternBuilder]
 */
class RegoLexerBasedTodoIndexer : LexerBasedTodoIndexer(){
    override fun createLexer(consumer: OccurrenceConsumer): Lexer = object : BaseFilterLexer(RegoLexerAdapter(), consumer) {
        override fun advance() {
            if (myDelegate.tokenType in REGO_COMMENT) {
                scanWordsInToken(UsageSearchContext.IN_COMMENTS.toInt(), false, false)
                advanceTodoItemCountsInToken()
            }
            myDelegate.advance()
        }
    }
}