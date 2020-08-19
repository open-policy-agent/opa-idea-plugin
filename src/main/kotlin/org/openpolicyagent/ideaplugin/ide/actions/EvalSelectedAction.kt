package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.openpolicyagent.ideaplugin.opa.tool.OpaActions
import org.openpolicyagent.ideaplugin.openapiext.isOPAPluginApplicable


class EvalSelectedAction : DumbAwareAction() {
    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = getProjectAndDocument(e) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val (project, document) = getProjectAndDocument(e) ?: return
        if (!document.isOPAPluginApplicable) {
            return
        }
        val editor = getEditor(e) ?: return
        OpaActions().evalSelection(project, document, editor)
    }
}