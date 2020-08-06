/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.psi

import com.intellij.psi.PsiFile

/**
 * Return [true] if the file is a rego test file (ie containing tests), [false] otherwise
 */
fun PsiFile.isRegoTestFile() : Boolean{
    return this is RegoFile && name.startsWith("test_")
}

/**
 * Return [true] if the file is a rego source file (ie not containing tests), [false] otherwise
 */
fun PsiFile.isRegoSourceFile() : Boolean{
    return this is RegoFile && ! name.startsWith("test_")
}