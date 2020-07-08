package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.execution.ExecutionException
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import org.openpolicyagent.ideaplugin.opa.tool.OpaCheck
import org.openpolicyagent.ideaplugin.openapiext.isUnitTestMode



class CheckAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val (project, document) = getProjectAndDocument(e) ?: return
        val error = checkDocumentSafe(project, document)
        //if (error != null)
    }

    /**
     * Runs OpaCheck.checkDocument, catching errors
     */
    private fun checkDocumentSafe(project: Project, document: Document): String? {
        return try {
            OpaCheck().checkDocument(project, document)
        } catch (e: ExecutionException) {
            // Just easy way to know that something wrong happened
            if (isUnitTestMode) throw e
            null
        }
    }
}