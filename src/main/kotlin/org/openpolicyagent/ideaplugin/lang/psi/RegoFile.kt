/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import org.openpolicyagent.ideaplugin.lang.RegoFileType
import org.openpolicyagent.ideaplugin.lang.RegoLanguage

class RegoFile(fileViewProvider: FileViewProvider) : PsiFileBase(fileViewProvider, RegoLanguage) {
    override fun getFileType(): FileType = RegoFileType

    override fun toString(): String = "Rego File"

}

val VirtualFile.isNotRegoFile: Boolean get() = !isRegoFile
val VirtualFile.isRegoFile: Boolean get() = fileType == RegoFileType