/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.typing

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.openpolicyagent.ideaplugin.lang.psi.RegoTypes

/**
 * Defines the brace matching for Rego language (ie allow to auto close  brace, bracket and parenthesis when typed, also
 * color matching braces, brackets...)
 */
class RegoBraceMatcher: PairedBraceMatcher {
    override fun getPairs() = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        private val PAIRS: Array<BracePair> = arrayOf(
            BracePair(RegoTypes.LBRACE, RegoTypes.RBRACE, true),
            BracePair(RegoTypes.LBRACK, RegoTypes.RBRACK, false),
            BracePair(RegoTypes.LPAREN, RegoTypes.RPAREN, false)
        )
    }
}
