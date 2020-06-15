/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.opa.tool

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import org.openpolicyagent.ideaplugin.lang.psi.isNotRegoFile
import org.openpolicyagent.ideaplugin.openapiext.execute
import org.openpolicyagent.ideaplugin.openapiext.isUnitTestMode
import org.openpolicyagent.ideaplugin.openapiext.virtualFile

/**
 * Utility class to format Rego file with opaFmt
 *
 * @see org.openpolicyagent.ideaplugin.ide.actions.FmtAction
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