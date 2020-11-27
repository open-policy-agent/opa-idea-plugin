/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.typing


import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import org.openpolicyagent.ideaplugin.lang.psi.RegoTypes

class RegoQuoteHandler: SimpleTokenSetQuoteHandler(RegoTypes.STRING_TOKEN, RegoTypes.RAW_STRING){
}