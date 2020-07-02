package org.openpolicyagent.ideaplugin.opa.tool

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.calcRelativeToProjectPath
import com.intellij.openapi.wm.ToolWindowManager
import org.openpolicyagent.ideaplugin.ide.extensions.OPAActionToolWindow
import org.openpolicyagent.ideaplugin.lang.psi.isRegoFile
import org.openpolicyagent.ideaplugin.openapiext.virtualFile

/**
 * Utility class to format Rego file with opaFmt
 *
 * @see org.openpolicyagent.ideaplugin.ide.actions.CheckAction
 */
class OpaActions : OpaBaseTool() {

    /**
     * Opens window running opa test on the current file, or popup if current
     * file is not a rego file
     */

    fun checkDocument(project: Project, document: Document, editor: Editor) {
        val file = document.virtualFile
        if (file != null && file.isRegoFile && file.isValid) {
            val opaWindow = OPAActionToolWindow()
            // todo: get path to file relative to project path
            //  val path_to_file = calcRelativeToProjectPath(file, project)
            val args = mutableListOf("check", file.name)
            opaWindow.runProcessInConsole(project, args, "Opa Check")
        } else {
            //todo: currently it appears this does nothing :(
            HintManager.getInstance().showErrorHint(editor, "Current file not valid or not Rego file")
        }

    }

    /**
     * Opens window running opa test --verbose on project directory
     */
     fun testWorkspace(project: Project, document: Document, editor: Editor) {
            val opaWindow = OPAActionToolWindow()
            val args = mutableListOf("test", ".", "--verbose")
            opaWindow.runProcessInConsole(project, args, "Opa Test")
            //todo: possibly check if project contains no rego files?
    }

    fun testWorkspaceCoverage(project: Project, document: Document, editor: Editor) {
        val opaWindow = OPAActionToolWindow()
        val args = mutableListOf("test", "--coverage", "--format=json", ".")
        opaWindow.runProcessInConsole(project, args, "Opa Test Coverage")
        //todo: possibly check if project contains no rego files?
    }

}