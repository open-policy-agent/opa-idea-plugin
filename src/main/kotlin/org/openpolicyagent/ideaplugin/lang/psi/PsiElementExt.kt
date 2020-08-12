/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * return the [org.openpolicyagent.ideaplugin.lang.psi.RegoPackage] of the file or null if the file is not a
 * [org.openpolicyagent.ideaplugin.lang.psi.RegoFile] or does not contains a regoPackage
 */
fun PsiElement.getRegoPackage(): RegoPackage?{
    if (containingFile is RegoFile) {
        return PsiTreeUtil.findChildOfType(containingFile, RegoPackage::class.java)
    }
    return null
}


inline fun <reified T : PsiElement> PsiElement.ancestorOrSelf(): T? =
    PsiTreeUtil.getParentOfType(this, T::class.java, /* strict */ false)
