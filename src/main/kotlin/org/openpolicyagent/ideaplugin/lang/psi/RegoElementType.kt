/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.psi

import com.intellij.psi.tree.IElementType
import org.openpolicyagent.ideaplugin.lang.RegoLanguage

class RegoElementType(debugName: String): IElementType(debugName, RegoLanguage) {
    override fun toString(): String = "RegoElementType." + super.toString()
}