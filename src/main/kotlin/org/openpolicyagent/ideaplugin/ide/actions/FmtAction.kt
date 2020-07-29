/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.execution.ExecutionException
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import org.openpolicyagent.ideaplugin.opa.tool.OpaFmt
import org.openpolicyagent.ideaplugin.openapiext.*


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

}