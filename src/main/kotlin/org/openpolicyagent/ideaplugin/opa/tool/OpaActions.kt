/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.opa.tool

import com.intellij.codeInsight.hint.HintManager
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.openpolicyagent.ideaplugin.ide.extensions.OPAActionToolWindow
import org.openpolicyagent.ideaplugin.lang.psi.isRegoFile
import org.openpolicyagent.ideaplugin.openapiext.execute
import org.openpolicyagent.ideaplugin.openapiext.isUnitTestMode
import org.openpolicyagent.ideaplugin.openapiext.virtualFile

/**
 * Utility class that exposes various OPA actions as functions
 */
class OpaActions : OpaBaseTool() {

    /**
     * Returns the text of the [document] formatted with `opa fmt` command
     * returns empty string if an error occurs
     */
    @Throws(ExecutionException::class)
    fun reformatDocument(project: Project, document: Document): String {
        val processOutput = try {
            GeneralCommandLine(opaBinary)
                .withWorkDirectory(project.basePath)
                .withParameters("fmt")
                .withCharset(Charsets.UTF_8)
                .execute(project, false, stdIn = document.text.toByteArray())
        } catch (e: ExecutionException) {
            if (isUnitTestMode) throw e else return ""
        }

        return processOutput.stdout
    }

    /**
     * Opens window running `opa test` on the current file
     * or displays popup if current file is not a rego file
     */

    fun checkDocument(project: Project, document: Document) {
        val file = document.virtualFile ?: return
        // todo: get path to file relative to project path
        val args = mutableListOf("check", file.name)
        OPAActionToolWindow().runProcessInConsole(project, args, "Opa Check")
    }

    /**
     * Opens window running `opa test --verbose` on project directory
     */
    fun testWorkspace(project: Project) {
        val opaWindow = OPAActionToolWindow()
        val args = mutableListOf("test", ".", "--verbose")
        opaWindow.runProcessInConsole(project, args, "Opa Test")
        //todo: possibly check if project contains no rego files?
    }

    fun testWorkspaceCoverage(project: Project) {
        val opaWindow = OPAActionToolWindow()
        val args = mutableListOf("test", "--coverage", "--format=json", ".")
        opaWindow.runProcessInConsole(project, args, "Opa Test Coverage")
        //todo: possibly check if project contains no rego files?
    }

}