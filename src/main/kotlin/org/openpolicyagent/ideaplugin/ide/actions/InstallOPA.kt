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
        //make sure opa binary doesn't already exist
       if(PathEnvironmentVariableUtil.findInPath(OpaBaseTool.opaBinary) != null){
           if(e.project != null) {
                Notification("ActionNotPerformed", "Install OPA", "OPA binary already exists", NotificationType.WARNING).notify(e.project)
            }
            return
        }
        val project = e.project ?: return
       val logconsole = OPAActionToolWindow().getLogConsole(project, "Install OPA")
        logconsole.print("Finding destination...\n", ConsoleViewContentType.LOG_INFO_OUTPUT)

        val directory = getWriteableDirectoryInPath()
        if(directory == null){
            logconsole.print("No writeable directory found in PATH\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
        }

        logconsole.print("Installing Opa binary\n", ConsoleViewContentType.LOG_INFO_OUTPUT)

        val url = if (SystemInfo.isLinux) "https://openpolicyagent.org/downloads/latest/opa_linux_amd64"
                        else "https://openpolicyagent.org/downloads/latest/opa_darwin_amd64"

        val execFile = File(directory, "opa")

        //downloading OPA binary to first writeable directory in PATH
        var progbarpct = 0
        Fuel.download(url)
                .fileDestination { _, _ -> execFile }
                .progress { readBytes, totalBytes ->
                    //progress bar
                   if(readBytes.toFloat()/totalBytes.toFloat() * 100 > progbarpct){
                        logconsole.print(".", ConsoleViewContentType.LOG_INFO_OUTPUT)
                       progbarpct += 5
                    }
                }
                .response { result  ->
                    when(result){
                        is Result.Failure -> {
                            logconsole.print(result.getException().toString(), ConsoleViewContentType.ERROR_OUTPUT)
                        }
                        //on successful download, make execFile executable
                        is Result.Success -> {
                            logconsole.print("Done!\n Setting file to be executable...\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                            val success = execFile.setExecutable(true)
                            if(success){
                                logconsole.print("All set!\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                            } else {
                                logconsole.print("\nCould not set downloaded file to be excecutable\n", ConsoleViewContentType.ERROR_OUTPUT)
                            }
                        }
                    }
                }

    }

    //retrieves the first directory from PATH that is writeable or null if none exist
    private fun getWriteableDirectoryInPath(): String? {
        val dirs = PathEnvironmentVariableUtil.getPathVariableValue()?.let { PathEnvironmentVariableUtil.getPathDirs(it) }
            ?: return null
        for (dir in dirs){
            if(LocalFileSystem.getInstance().findFileByPath(dir)?.isWritable == true){
                return dir
            }
        }
        return null
    }
    companion object {
       val ID = "org.openpolicyagent.ideaplugin.actions.InstallOPA"
        val INSTANCE = ActionManager.getInstance().getAction(ID)!!
    }

}
