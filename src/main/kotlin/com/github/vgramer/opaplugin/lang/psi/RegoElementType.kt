package com.github.vgramer.opaplugin.lang.psi

import com.github.vgramer.opaplugin.lang.RegoLanguage
import com.intellij.psi.tree.IElementType

class RegoElementType(debugName: String): IElementType(debugName, RegoLanguage) {
    override fun toString(): String = "RegoElementType." + super.toString()
}