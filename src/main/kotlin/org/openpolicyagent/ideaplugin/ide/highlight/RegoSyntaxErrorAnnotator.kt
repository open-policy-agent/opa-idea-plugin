/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.openpolicyagent.ideaplugin.lang.psi.RegoString

class RegoSyntaxErrorAnnotator : AnnotatorBase() {
    override fun annotateInternal(element: PsiElement, holder: AnnotationHolder) {
        if (element is RegoString) {
            element.stringToken?.let {
                val text = it.text

                // Check that double quoted string is closed properly
                if (text.length <= 1 || text[0] != text[text.length - 1] || text[text.length - 2] == '\\') {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing quote").create()
                }
            }
            element.rawString?.let {
                val text = it.text

                // Check that raw string  is closed properly
                if (text.length <= 1 || text[0] != text[text.length - 1]) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing backtick").create()
                }
            }
        }
    }
}