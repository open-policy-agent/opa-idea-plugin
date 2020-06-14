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
    RegoTypes.PACKAGE_TOKEN,
    RegoTypes.IMPORT_TOKEN,
    RegoTypes.AS,
    RegoTypes.DEFAULT,
    RegoTypes.ELSE,
    RegoTypes.SOME,
    RegoTypes.NOT,
    RegoTypes.WITH
)

val REGO_OPERATOR = tokenSetOf(
    RegoTypes.PLUS,
    RegoTypes.MINUS,
    RegoTypes.MUL,
    RegoTypes.QUOTIENT,
    RegoTypes.REMAINDER,
    RegoTypes.BIT_OR,
    RegoTypes.BIT_AND,
    RegoTypes.EQ,
    RegoTypes.NOT_EQ,
    RegoTypes.LESS,
    RegoTypes.LESS_OR_EQUAL,
    RegoTypes.GREATHER,
    RegoTypes.GREATER_OR_EQUAL
)