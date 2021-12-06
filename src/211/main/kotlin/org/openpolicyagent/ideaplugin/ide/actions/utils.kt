/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.actions


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.openpolicyagent.ideaplugin.lang.RegoLanguage
import org.openpolicyagent.ideaplugin.lang.psi.RegoImport
import org.openpolicyagent.ideaplugin.lang.psi.RegoPackage
import org.openpolicyagent.ideaplugin.openapiext.virtualFile

/**
 * returns a nullable Pair containing the [Project] and the [Document]
 *
 * If the project is null or the file is not a Rego file then return null.
 */
fun getProjectAndDocument(e: AnActionEvent): Pair<Project, Document>? {
    val project = e.project ?: return null
    val editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) ?: getSelectedEditor(project) ?: return null
    val document = editor.document

    return Pair(project, document)

}

fun getEditor(e: AnActionEvent): Editor? {
    val project = e.project ?: return null
    return e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) ?: getSelectedEditor(project) ?: return null
}

fun getSelectedEditor(project: Project): Editor? =
    FileEditorManager.getInstance(project).selectedTextEditor

/**
 * Returns package name in document as string
 */
fun getPackageAsString(document: Document, project: Project): String {
    val file = document.virtualFile ?: return ""
    val fileView = PsiManager.getInstance(project).findViewProvider(file) ?: return ""
    val psiTree = fileView.getPsi(RegoLanguage)
    val children = psiTree.children
    for (child in children) {
        if (child is RegoPackage) {
            return child.ref.text
        }
    }
    return ""
}

/**
 * Returns a list of the names of the imports in document as strings
 */
fun getImportsAsString(document: Document, project: Project): MutableList<String> {
    val file = document.virtualFile ?: return mutableListOf<String>()
    val fileView = PsiManager.getInstance(project).findViewProvider(file) ?: return mutableListOf<String>()
    val psiTree = fileView.getPsi(RegoLanguage)
    val children = psiTree.children
    var imports = mutableListOf<String>()
    for (child in children) {
        if (child is RegoImport) {
            var str = child.ref.text
            val v = child.`var`
            if (v != null) {
                str += " as "
                str += v.text
            }
            imports.add(str)
        }
    }
    return imports
}

/**
 * Returns whether the project base directory has a file with name
 * as direct child (rather than in subdirectory)
 */
fun fileDirectChildOfRoot(project: Project, name: String): Boolean {
    val allfiles = FilenameIndex.getVirtualFilesByName(project, name, GlobalSearchScope.allScope(project))
    for (file in allfiles) {
        if (file.parent.path == project.basePath) {
            return true
        }
    }
    return false
}
