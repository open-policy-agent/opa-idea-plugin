package com.github.vgramer.opaplugin.lang.psi

import com.github.vgramer.opaplugin.lang.RegoFileType
import com.github.vgramer.opaplugin.lang.RegoLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class RegoFile(fileViewProvider: FileViewProvider) : PsiFileBase(fileViewProvider, RegoLanguage) {
    override fun getFileType(): FileType = RegoFileType

    override fun toString(): String = "Rego File"

}