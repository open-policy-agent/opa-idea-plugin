/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.opa.tool

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import org.opa.ideaplugin.lang.psi.isNotRegoFile
import org.opa.ideaplugin.opa.tool.OpaBaseTool
import org.opa.ideaplugin.openapiext.execute
import org.opa.ideaplugin.openapiext.isUnitTestMode
import org.opa.ideaplugin.openapiext.virtualFile
import org.opa.ideaplugin.opa.tool.OpaBaseTool.Companion.opaBinary

/**
 * Utility class to format Rego file with opaFmt
 *
 * @see org.opa.ideaplugin.ide.actions.FmtAction
 */
class OpaFmt : OpaBaseTool() {

    /**
     * Return the text of the [document] formatted with `opa fmt` command or null if an error occur or text is already well formatted
     */
    @Throws(ExecutionException::class)
    fun reformatDocument(project: Project, document: Document): String? {
        val file = document.virtualFile ?: return null

        if (file.isNotRegoFile || !file.isValid) {
            return null
        }

        return reformatText(project, document.text)
    }

    /**
     * Return the [text] formatted with `opa fmt` command or null if an error occur or text is already well formatted
     */
    @Throws(ExecutionException::class)
    private fun reformatText(project: Project, text: String): String? {

        val processOutput = try {
            GeneralCommandLine(opaBinary)
                .withWorkDirectory(project.basePath)
                .withParameters("fmt")
                .withCharset(Charsets.UTF_8)
                .execute(project, false, stdIn = text.toByteArray())
        } catch (e: ExecutionException) {
            if (isUnitTestMode) throw e else return null
        }

        // if the file is already formatted, opa return an empty string
        return processOutput.stdout.ifEmpty { null }
    }
}