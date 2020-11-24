/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.openpolicyagent.ideaplugin.ide.colors.RegoColor
import org.openpolicyagent.ideaplugin.lang.psi.RegoEmptySet
import org.openpolicyagent.ideaplugin.lang.psi.RegoExprCall
import org.openpolicyagent.ideaplugin.lang.psi.RegoRule
import org.openpolicyagent.ideaplugin.openapiext.isUnitTestMode

class RegoHighlighterAnnotator : Annotator {
    // visibility for Testing
    val usedColors = listOf(RegoColor.HEAD.textAttributesKey, RegoColor.CALL.textAttributesKey)

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val (style, range) = when (element) {
            is RegoRule -> Pair(RegoColor.HEAD, element.ruleHead.`var`.textRange)

            is RegoEmptySet -> Pair(RegoColor.CALL, element.textRange)

            is RegoExprCall -> {
                val varlist = element.refArgDotList
                val textRange = if (varlist.size >= 1) varlist[varlist.size - 1].`var`.textRange else element.`var`.textRange
                Pair(RegoColor.CALL, textRange)
            }

            else -> null
        } ?: return

        val severity = if (isUnitTestMode) style.testSeverity else HighlightSeverity.INFORMATION

        holder.newSilentAnnotation(severity)
            .range(range)
            .textAttributes(style.textAttributesKey)
            .create()
    }
}