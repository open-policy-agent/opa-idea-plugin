package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import org.openpolicyagent.ideaplugin.lang.psi.isNotRegoFile
import org.openpolicyagent.ideaplugin.openapiext.virtualFile


class ActionUtils {
    /**
     * return a nullable Pair congaing the [Project] and the [Document]
     *
     * If the project is null or the file is not a Rego file then return null
     */
    fun getProjectAndDocument(e: AnActionEvent): Pair<Project, Document>? {
        val project = e.project ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) ?: getSelectedEditor(project) ?: return null
        val document = editor.document
        val file = document.virtualFile ?: return null
        if (!file.isInLocalFileSystem || file.isNotRegoFile) return null


        return Pair(project, document)

    }

    fun getSelectedEditor(project: Project): Editor? =
            FileEditorManager.getInstance(project).selectedTextEditor
}
