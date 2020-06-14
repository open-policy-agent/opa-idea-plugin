/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.actions

import org.opa.ideaplugin.lang.psi.isNotRegoFile
import org.opa.ideaplugin.opa.tool.OpaFmt
import org.opa.ideaplugin.openapiext.*
import com.intellij.execution.ExecutionException
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project

class FmtAction : DumbAwareAction() {

    /**
     * Define if the action is enable and visible to the user.
     */
    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = getProjectAndDocument(e) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val (project, document) = getProjectAndDocument(e) ?: return
        checkWriteAccessNotAllowed()
        val formattedText = project.computeWithCancelableProgress("Reformatting File with Opa fmt...") {
            reformatDocumentAndGetText(project, document)
        } ?: return
        project.runWriteCommandAction { document.setText(formattedText) }
    }

    private fun reformatDocumentAndGetText(project: Project, document: Document): String? {
        return try {
            // TODO check opa binary in path
            OpaFmt().reformatDocument(project, document)
        } catch (e: ExecutionException) {
            // Just easy way to know that something wrong happened
            if (isUnitTestMode) throw e
            null
        }
    }

    /**
     * return a nullable Pair congaing the [Project] and the [Document]
     *
     * If the project is null or the file is not a Rego file then return null
     */
    private fun getProjectAndDocument(e: AnActionEvent): Pair<Project, Document>? {
        val project = e.project ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) ?: getSelectedEditor(project) ?: return null
        val document = editor.document
        val file = document.virtualFile ?: return null
        if (!file.isInLocalFileSystem || file.isNotRegoFile) return null


        return Pair(project, document)

    }

    private fun getSelectedEditor(project: Project): Editor? =
        FileEditorManager.getInstance(project).selectedTextEditor

}