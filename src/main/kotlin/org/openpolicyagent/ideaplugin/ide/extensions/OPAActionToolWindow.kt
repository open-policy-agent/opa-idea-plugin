package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.execution.actions.StopProcessAction
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.impl.ContentImpl
import org.openpolicyagent.ideaplugin.opa.tool.OpaBaseTool
import java.awt.BorderLayout
import java.util.concurrent.ExecutionException
import javax.swing.JPanel

class OPAActionToolWindow {

    private val OPA_CONSOLE_ID = "OPA Console"
    private val OPA_CONSOLE_NAME = OPA_CONSOLE_ID

    fun runProcessInConsole(project: Project, parameters: MutableList<String>, title: String) {
        val commandLine = GeneralCommandLine()
            .withExePath(OpaBaseTool.opaBinary)
            .withWorkDirectory(project.basePath)
            .withParameters(parameters)
            .withCharset(Charsets.UTF_8)

        try {
            val processHandler = OSProcessHandler(commandLine)
            ProcessTerminatedListener.attach(processHandler)

            ApplicationManager.getApplication().invokeLater {
                val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
                consoleView.clear()
                consoleView.attachToProcess(processHandler)
                processHandler.startNotify()

                val toolWindowManager = ToolWindowManager.getInstance(project)
                var toolWindow = toolWindowManager.getToolWindow(OPA_CONSOLE_ID)

                val panel = JPanel(BorderLayout())
                panel.add(consoleView.component, "Center")
                val toolbarActions = DefaultActionGroup()
                toolbarActions.addAll(consoleView.createConsoleActions().copyOf().toList())
                toolbarActions.add(StopProcessAction("Stop Process", "Stop Process", processHandler))
                val toolbar = ActionManager.getInstance().createActionToolbar("unknown", toolbarActions, false)
                toolbar.setTargetComponent(consoleView.component)

                val consoleContent = ContentImpl(panel, title, false)


                if (toolWindow != null) {
                    val existing = toolWindow.contentManager.findContent(title) ?: null
                    if (existing != null) {
                        toolWindow.contentManager.removeContent(existing, true)
                    }

                    attachAndShowConsole(consoleContent, toolWindow)
                    return@invokeLater
                }

                // Create and register the OPA window
                toolWindow = toolWindowManager.registerToolWindow(OPA_CONSOLE_ID, true, ToolWindowAnchor.BOTTOM)
                toolWindow.title = OPA_CONSOLE_NAME
                toolWindow.stripeTitle = OPA_CONSOLE_NAME
                toolWindow.isShowStripeButton = true
                toolWindow.icon = AllIcons.Toolwindows.ToolWindowMessages

                attachAndShowConsole(consoleContent, toolWindow)
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    //helper to attach console window running porcess to opa tool window
    private fun attachAndShowConsole(consoleContent: ContentImpl, toolWindow: ToolWindow) {
        consoleContent.manager = toolWindow.contentManager
        toolWindow.contentManager.addContent(consoleContent)
        toolWindow.contentManager.setSelectedContent(consoleContent)

        toolWindow.show(null)
    }
}