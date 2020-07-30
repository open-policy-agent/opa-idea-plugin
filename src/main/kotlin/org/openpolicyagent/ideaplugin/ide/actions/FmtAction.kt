/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.openpolicyagent.ideaplugin.opa.tool.OpaActions
import org.openpolicyagent.ideaplugin.openapiext.*


class FmtAction : DumbAwareAction() {

    /**
     * Defines if the action is enabled and visible to the user.
     */
    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = getProjectAndDocument(e) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val (project, document) = getProjectAndDocument(e) ?: return
        if (!document.isOPAPluginApplicable) {
            return
        }
        checkWriteAccessNotAllowed()
        val formattedText = project.computeWithCancelableProgress("Reformatting File with opa fmt ...") {
            OpaActions().reformatDocument(project, document)
        }
        if (formattedText.isNotBlank()) {
            project.runWriteCommandAction { document.setText(formattedText) }
        }
    }

}