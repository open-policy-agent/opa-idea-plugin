/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import org.openpolicyagent.ideaplugin.lang.RegoIcons

class RegoCreateFileAction : CreateFileFromTemplateAction(NAME, "", RegoIcons.OPA), DumbAware {

    companion object {
        private const val NAME = "Rego File"
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) = NAME

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(NAME)
            // keep templateName sync with files in /src/main/resources/fileTemplates/internal
            .addKind("Empty File", RegoIcons.OPA, "Rego File")
    }
}