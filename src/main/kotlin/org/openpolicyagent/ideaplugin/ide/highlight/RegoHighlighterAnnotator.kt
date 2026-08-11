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

class RegoHighlighterAnnotator : AnnotatorBase() {
    // visibility for Testing
    val usedColors = listOf(RegoColor.HEAD.textAttributesKey, RegoColor.CALL.textAttributesKey)

    override fun annotateInternal(element: PsiElement, holder: AnnotationHolder) {
        val (style, range) = when (element) {
            is RegoRule -> {
                // After grammar update for v1 syntax, the var lives inside head-ref under either
                // a regular rule-head or a partial-set-rule.
                val headRef = element.ruleHead?.headRef ?: element.partialSetRule?.headRef
                headRef?.let { Pair(RegoColor.HEAD, it.textRange) }
            }

            is RegoEmptySet -> Pair(RegoColor.CALL, element.textRange)

            is RegoExprCall -> {
                // a ref-arg-dot holds no var when it is a keyword, as in `data.foo.not`,
                // in which case the call name falls back to the root var
                val textRange = element.refArgDotList.lastOrNull()?.`var`?.textRange ?: element.`var`.textRange
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