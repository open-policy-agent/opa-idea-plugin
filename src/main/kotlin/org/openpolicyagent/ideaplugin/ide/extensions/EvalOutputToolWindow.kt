package org.openpolicyagent.ideaplugin.ide.extensions


import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessNotCreatedException
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.impl.ContentImpl
import org.openpolicyagent.ideaplugin.ide.actions.InstallOPA
import org.openpolicyagent.ideaplugin.opa.tool.OpaBaseTool
import org.openpolicyagent.ideaplugin.openapiext.execute
import java.util.concurrent.ExecutionException


class EvalOutputToolWindow {
    private val OUTPUT_WINDOW_ID = "Output"
    /**
     * Returns (and creates if uninstantiated) the opa output tool window
     */
    private fun getOutputWindow(project: Project): ToolWindow {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        var toolWindow = toolWindowManager.getToolWindow(OUTPUT_WINDOW_ID)
        if (toolWindow == null){
            toolWindow = toolWindowManager.registerToolWindow(OUTPUT_WINDOW_ID, true, ToolWindowAnchor.RIGHT)
            toolWindow.title = OUTPUT_WINDOW_ID
            toolWindow.stripeTitle = OUTPUT_WINDOW_ID
            toolWindow.isShowStripeButton = true
            toolWindow.icon = AllIcons.Toolwindows.ToolWindowMessages
        }
        return toolWindow
    }

    fun showOutput(project: Project, parameters: MutableList<String>, title: String){
        val commandLine =
            GeneralCommandLine()
                .withExePath(OpaBaseTool.opaBinary)
                .withWorkDirectory(project.basePath)
                .withParameters(parameters)
                .withCharset(Charsets.UTF_8)
        val handler = try {
            OSProcessHandler(commandLine)
        } catch (e: ProcessNotCreatedException) {
            //Suggest to install opa binary in case of failure
            val notification = Notification("ActionNotPerformed", title, e.localizedMessage, NotificationType.ERROR)
            notification.addAction(InstallOPA.INSTANCE)
            notification.notify(project)
            return
        }
        ApplicationManager.getApplication().invokeLater {
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            val toolWindow = getOutputWindow(project)
            val consoleContent = ContentImpl(consoleView.component, title, false)

            //the tool window shouldn't have two consoles with the sdame title (task)
            val existing = toolWindow.contentManager.findContent(title) ?: null
            if (existing != null) {
                toolWindow.contentManager.removeContent(existing, true)
            }

            handler.addProcessListener(EvalListener(consoleView))
            handler.startNotify()

            consoleContent.manager = toolWindow.contentManager
            toolWindow.contentManager.addContent(consoleContent)
            toolWindow.contentManager.setSelectedContent(consoleContent)

            toolWindow.show(null)
            return@invokeLater
        }


//
//            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
//            val toolWindow = getOutputWindow(project)
//            val consoleContent = ContentImpl(consoleView.component, title, false)
//            consoleContent.manager = toolWindow.contentManager
//            toolWindow.contentManager.addContent(consoleContent)
//            toolWindow.contentManager.setSelectedContent(consoleContent)
//            val exitcode = processOutput.exitCode
//            if (exitcode != 0) {
//                if (processOutput.stdout != "") {
//                    consoleView.print(processOutput.stdout, ConsoleViewContentType.ERROR_OUTPUT)
//                } else {
//                    consoleView.print(processOutput.stderr, ConsoleViewContentType.ERROR_OUTPUT)
//                }
//            } else {
//                val parser = Parser.default()
//                val stringBuilder: StringBuilder = StringBuilder(processOutput.stdout)
//                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
//                //get json.result[0]
//                //consoleView.print("${json.string("result")}", ConsoleViewContentType.NORMAL_OUTPUT)
//            }


        //toolWindow.show(null)


    }
}