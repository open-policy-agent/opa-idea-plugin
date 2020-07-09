//todo: this solution is currently causing a race
// (occasionally get java.lang.Throwable: Synchronous execution on EDT
// in internal IDE Error log) but it works....

package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import org.openpolicyagent.ideaplugin.opa.tool.OpaBaseTool
import org.openpolicyagent.ideaplugin.openapiext.execute

class OPAActionToolWindow(toolWindow: ToolWindow) {

    val window = toolWindow

    /**
     * Runs a command line process in OPA Console Tool Window logging process output
     */
    fun runProcessInConsole(project: Project, args: MutableList<String>, title: String) {
        val process = GeneralCommandLine(OpaBaseTool.opaBinary) //todo: still haven't verified opa binary is in path
                .withWorkDirectory(project.basePath)
                .withParameters(args)
                .withCharset(Charsets.UTF_8)
        val handler = OSProcessHandler(process)


        val consoleWindow = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

        consoleWindow.attachToProcess(handler)

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(consoleWindow.component, title, false)
        content.isCloseable = true
        window.contentManager.removeAllContents(true)
        window.contentManager.addContent(content)

        handler.startNotify()

        val output = try {
            process.execute(project, false)
            "SUCCESS"
        } catch (e: Exception) {
            "FAIL"
        }
        window.show {
            consoleWindow.print(output, ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

}