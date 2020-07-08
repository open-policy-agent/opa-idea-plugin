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
 * @see org.openpolicyagent.ideaplugin.ide.actions.CheckAction
 */
class OpaCheck : OpaBaseTool() {

    /**
     * Returns the errors produced by opa check on [document]  or null if there are no errors
     */
    @Throws(ExecutionException::class)
    fun checkDocument(project: Project, document: Document): String? {
        val file = document.virtualFile ?: return null

        if (file.isNotRegoFile || !file.isValid) {
            return null
        }

        return checkFile(project, document.text)
    }

    /**
     * Returns the errors produced by opa check or null if opa check is successful
     */
    @Throws(ExecutionException::class)
    private fun checkFile(project: Project, text: String): String? {
        val processOutput = try {
            GeneralCommandLine(opaBinary) //todo: still haven't verified opa binary is in path
                    .withWorkDirectory(project.basePath)
                    .withParameters("check")
                    .withCharset(Charsets.UTF_8)
                    .execute(project, false, stdIn = text.toByteArray())
        } catch (e: ExecutionException) {
            if (isUnitTestMode) throw e else return null
        }
        // if the file is all set, return null, else processOutput
        return processOutput.stdout.ifEmpty { null }
    }
}