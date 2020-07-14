package org.openpolicyagent.ideaplugin.opa.tool

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import org.openpolicyagent.ideaplugin.ide.extensions.OPAActionToolWindow
import org.openpolicyagent.ideaplugin.lang.psi.isRegoFile
import org.openpolicyagent.ideaplugin.openapiext.virtualFile

/**
 * Utility class to format Rego file with opaFmt
 *
 * @see org.openpolicyagent.ideaplugin.ide.actions.CheckAction
 */
class OpaCheck : OpaBaseTool() {

    /**
     * Returns the errors produced by opa check on [document]  or null if there are no errors
     */

    fun checkDocument(project: Project, document: Document, editor: Editor) {
        val file = document.virtualFile

        if (file != null && file.isRegoFile && file.isValid) {
            checkFile(project, file.name)
        } else {
            //todo: currently it appears this does nothing :(
            HintManager.getInstance().showErrorHint(editor, "Current file not valid or not Rego file")
        }

    }

    /**
     * Returns the errors produced by opa check or null if opa check is successful
     */
    private fun checkFile(project: Project, name: String) {
        val opaWindow = OPAActionToolWindow()
        val args = mutableListOf("check", name)
        opaWindow.runProcessInConsole(project, args, "Opa Check")
    }
}