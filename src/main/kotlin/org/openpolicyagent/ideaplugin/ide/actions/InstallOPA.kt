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
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import java.io.File


class InstallOPA : DumbAwareAction(){
    override fun actionPerformed(e: AnActionEvent) {
        if(PathEnvironmentVariableUtil.findInPath(OpaBaseTool.opaBinary) != null){
            if(e.project != null) {
                Notification("ActionNotPerformed", "Install OPA", "OPA binary already exists", NotificationType.WARNING).notify(e.project)
            }
            return
        }
//        GeneralCommandLine("sh", "-c", "curl -s https://api.github.com/repos/open-policy-agent/opa/releases/latest |")
        val project = e.project ?: return
        val logconsole = OPAActionToolWindow().getLogConsole(project, "Install OPA")
        logconsole.print("\nFinding PATH...\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        val directory = getWriteableDirectoryInPath()
        if(directory == null){
            logconsole.print("\nNo writeable directory found in PATH\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
        }

        logconsole.print("\nInstalling Opa binary\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        val url = if (SystemInfo.isLinux) "https://openpolicyagent.org/downloads/latest/opa_linux_amd64"
                        else "https://openpolicyagent.org/downloads/latest/opa_darwin_amd64"
        val execFile = File(directory, "opa")
        var progbar= 0
        Fuel.download(url)
                .fileDestination { _, _ -> execFile }
                .progress { readBytes, totalBytes ->
                    //progress bar
                    if(readBytes.toFloat()/totalBytes.toFloat() * 10 > progbar){
                        progbar += 1
                        logconsole.print(".", ConsoleViewContentType.LOG_INFO_OUTPUT)
                    }
                }
                .response { result  ->
                    when(result){
                        is Result.Failure -> {
                            logconsole.print(result.getException().toString(), ConsoleViewContentType.ERROR_OUTPUT)
                        }
                        is Result.Success -> {
                            logconsole.print("\nDone! Setting file to be executable\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                            val success = execFile.setExecutable(true)
                            if(success){
                                logconsole.print("All set!\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                            } else {
                                logconsole.print("\nCould not set ./opa to be excecutable\n", ConsoleViewContentType.ERROR_OUTPUT)
                            }
                        }
                    }
                }


//        val command1 = GeneralCommandLine("curl",  "-L", "-o", "opa", url).withWorkDirectory()
//        val getopahandler = OSProcessHandler(command1)

//        ProcessTerminatedListener.attach(getopahandler)
//
//        ApplicationManager.getApplication().invokeLater {
//            logconsole.attachToProcess(getopahandler)
//            getopahandler.startNotify()
//            return@invokeLater
//        }

    }

    private fun getWriteableDirectoryInPath(): String? {

        val dirs = PathEnvironmentVariableUtil.getPathVariableValue()?.let { PathEnvironmentVariableUtil.getPathDirs(it) }
        if (dirs == null) {
            return null
        }
        for (dir in dirs){
            if(LocalFileSystem.getInstance().findFileByPath(dir)?.isWritable == true){
                return dir
            }
        }
        return null
    }

    //todo: this is hacky and gets called over and over -- ideal thing is figure out how to execute two processes
    // successively in actionperformed
//    override fun update(e: AnActionEvent) {
//        if(PathEnvironmentVariableUtil.findInPath(OpaBaseTool.opaBinary) != null){
//            return
//        }
//        val directory = PathEnvironmentVariableUtil.getPathVariableValue()?.let { PathEnvironmentVariableUtil.getPathDirs(it) }
//
//        val command1 = GeneralCommandLine("chmod", "755", "./opa").withWorkDirectory(directory?.firstOrNull())
//        val getopahandler = OSProcessHandler(command1)
//
//        ProcessTerminatedListener.attach(getopahandler)
//
//        ApplicationManager.getApplication().invokeLater {
//            getopahandler.startNotify()
//            return@invokeLater
//        }
//    }


    companion object {
        val ID = "org.openpolicyagent.ideaplugin.actions.InstallOPA"
        val INSTANCE = ActionManager.getInstance().getAction(ID)!!
    }

}