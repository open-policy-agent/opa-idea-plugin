/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// borrow to https://github.com/intellij-rust/intellij-rust/blob/master/common/src/test/kotlin/com/intellij/ide/annotator/TestSeverityProvider.kt
package org.openpolicyagent.ideaplugin.ide.highlight

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.SeveritiesProvider
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement

/**
 * Register new Severity for annotation. It allow to test the Highlighting of salient annotation
 * see [org.openpolicyagent.ideaplugin.ide.highlight.AnnotatorTestBase.setUp] for more information
 */
class TestSeverityProvider(private val severities: List<HighlightSeverity>) : SeveritiesProvider() {
    override fun getSeveritiesHighlightInfoTypes(): List<HighlightInfoType> = severities.map(::TestHighlightingInfoType)
}

private class TestHighlightingInfoType(private val severity: HighlightSeverity) : HighlightInfoType {
    override fun getAttributesKey(): TextAttributesKey = DEFAULT_TEXT_ATTRIBUTES
    override fun getSeverity(psiElement: PsiElement?): HighlightSeverity = severity

    companion object {
        private val DEFAULT_TEXT_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("DEFAULT_TEXT_ATTRIBUTES")
    }
}