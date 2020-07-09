/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.lang.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.opa.ideaplugin.lang.RegoLanguage

class RegoTokenType(debugName: String) : IElementType(debugName, RegoLanguage)


fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)

val REGO_KEYWORDS = tokenSetOf(
    RegoTypes.PACKAGE_KW,
    RegoTypes.IMPORT_KW,
    RegoTypes.AS_KW,
    RegoTypes.DEFAULT_KW,
    RegoTypes.ELSE_KW,
    RegoTypes.SOME_KW,
    RegoTypes.NOT_KW,
    RegoTypes.WITH_KW
)

val REGO_OPERATOR = tokenSetOf(
    RegoTypes.PLUS,
    RegoTypes.MINUS,
    RegoTypes.MULTIPLY,
    RegoTypes.DIVIDE,
    RegoTypes.REMAINDER,
    RegoTypes.OR,
    RegoTypes.AND,
    RegoTypes.EQ,
    RegoTypes.NEQ,
    RegoTypes.LT,
    RegoTypes.LTE,
    RegoTypes.GT,
    RegoTypes.GTE
)