/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// borrow to https://github.com/intellij-rust/intellij-rust/blob/master/common/src/main/kotlin/com/intellij/ide/annotator/AnnotatorBase.kt
package org.openpolicyagent.ideaplugin.ide.highlight


import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiElement
import com.intellij.util.containers.ContainerUtil
import org.jetbrains.annotations.TestOnly
import org.openpolicyagent.ideaplugin.openapiext.isUnitTestMode

/**
 * Abstract annotator that add annotation only if
 *  * it not run in test mode
 *  * it run in test and annotator is registered by testing class
 *
 * It simplify the tests because only the class under test adds annotations. So in test output, we get only the highlighting
 * of the class under test.
 */
abstract class AnnotatorBase : Annotator {

    final override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!isUnitTestMode || javaClass in enabledAnnotators) {
            annotateInternal(element, holder)
        }
    }

    /**
     * Annotates the specified PSI element.
     * for more info see [com.intellij.lang.annotation.Annotator.annotate]
     */
    protected abstract fun annotateInternal(element: PsiElement, holder: AnnotationHolder)

    companion object {
        private val enabledAnnotators: MutableSet<Class<out AnnotatorBase>> = ContainerUtil.newConcurrentSet()

        @TestOnly
        fun enableAnnotator(annotatorClass: Class<out AnnotatorBase>, parentDisposable: Disposable) {
            enabledAnnotators += annotatorClass
            Disposer.register(parentDisposable, Disposable { enabledAnnotators -= annotatorClass })
        }
    }
}