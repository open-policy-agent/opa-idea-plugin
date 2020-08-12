package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.openpolicyagent.ideaplugin.opa.tool.OpaActions


class TraceSelectionAction : DumbAwareAction() {
    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = getProjectAndDocument(e) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val (project, document) = getProjectAndDocument(e) ?: return
        val editor = getEditor(e) ?: return
        OpaActions().traceSelection(project, document, editor)
    }
}