package org.openpolicyagent.ideaplugin.ide.actions

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.openpolicyagent.ideaplugin.ide.extensions.OPAActionToolWindow
import org.openpolicyagent.ideaplugin.opa.tool.OpaBaseTool
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.SystemInfo

class InstallOPA : DumbAwareAction(){
    override fun actionPerformed(e: AnActionEvent) {
        if(PathEnvironmentVariableUtil.findInPath(OpaBaseTool.opaBinary) != null){
            if(e.project != null) {
                Notification("ActionNotPerformed", "Install OPA", "OPA binary already exists", NotificationType.WARNING).notify(e.project)
            }
            return
        }
        GeneralCommandLine("sh", "-c", "curl -s https://api.github.com/repos/open-policy-agent/opa/releases/latest |")
        val project = e.project ?: return
        val logconsole = OPAActionToolWindow().getLogConsole(project, "Install OPA")
        logconsole.print("\nInstalling Opa binary\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        val url = if (SystemInfo.isLinux) "https://openpolicyagent.org/downloads/latest/opa_linux_amd64"
                            else "https://openpolicyagent.org/downloads/latest/opa_darwin_amd64"
        val directory = PathEnvironmentVariableUtil.getPathVariableValue()?.let { PathEnvironmentVariableUtil.getPathDirs(it) }
        val command1 = GeneralCommandLine("curl",  "-L", "-o", "opa", url).withWorkDirectory(directory?.firstOrNull())
        val getopahandler = OSProcessHandler(command1)

        ProcessTerminatedListener.attach(getopahandler)

        ApplicationManager.getApplication().invokeLater {
            logconsole.attachToProcess(getopahandler)
            getopahandler.startNotify()
            return@invokeLater
        }

    }

    //todo: this is hacky and gets called over and over -- ideal thing is figure out how to execute two processes
    // successively in actionperformed
    override fun update(e: AnActionEvent) {
        if(PathEnvironmentVariableUtil.findInPath(OpaBaseTool.opaBinary) != null){
            return
        }
        val directory = PathEnvironmentVariableUtil.getPathVariableValue()?.let { PathEnvironmentVariableUtil.getPathDirs(it) }

        val command1 = GeneralCommandLine("chmod", "755", "./opa").withWorkDirectory(directory?.firstOrNull())
        val getopahandler = OSProcessHandler(command1)

        ProcessTerminatedListener.attach(getopahandler)

        ApplicationManager.getApplication().invokeLater {
            getopahandler.startNotify()
            return@invokeLater
        }
    }


    companion object {
        val ID = "org.openpolicyagent.ideaplugin.actions.InstallOPA"
        val INSTANCE = ActionManager.getInstance().getAction(ID)!!
    }

}