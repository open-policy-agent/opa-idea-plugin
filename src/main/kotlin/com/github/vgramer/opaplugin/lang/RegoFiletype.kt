package com.github.vgramer.opaplugin.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


object RegoFileType : LanguageFileType(RegoLanguage) {
    override fun getName(): String = "Rego file"

    override fun getDescription(): String = "Rego language file"

    override fun getDefaultExtension(): String = "rego"

    override fun getIcon(): Icon = RegoIcons.OPA

}