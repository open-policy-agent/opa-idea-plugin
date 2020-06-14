/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.lang.psi

import org.opa.ideaplugin.lang.RegoFileType
import org.opa.ideaplugin.lang.RegoLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider

class RegoFile(fileViewProvider: FileViewProvider) : PsiFileBase(fileViewProvider, RegoLanguage) {
    override fun getFileType(): FileType = RegoFileType

    override fun toString(): String = "Rego File"

}


val VirtualFile.isNotRegoFile: Boolean get() = !isRegoFile
val VirtualFile.isRegoFile: Boolean get() = fileType == RegoFileType