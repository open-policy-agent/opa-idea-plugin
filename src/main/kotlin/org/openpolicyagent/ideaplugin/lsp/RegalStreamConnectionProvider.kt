/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// Implementation based on LSP4IJ Developer Guide:
// https://github.com/redhat-developer/lsp4ij/blob/035c4fd82cc7603ef3a346be0d9585d4e41564b0/docs/dap/DeveloperGuide.md

package org.openpolicyagent.ideaplugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import org.openpolicyagent.ideaplugin.opa.project.settings.OpaProjectSettings
import org.openpolicyagent.ideaplugin.util.RegalExecutableUtil

class RegalStreamConnectionProvider(private val project: Project) : OSProcessStreamConnectionProvider() {
    init {
        val commandLine = GeneralCommandLine()
        val regalPath = RegalExecutableUtil.findRegalExecutable(project)
        if (regalPath == null) {
            showRegalNotFoundNotification()
            commandLine.exePath = RegalExecutableUtil.regalBinary
        } else {
            commandLine.exePath = regalPath
        }
        val settings = OpaProjectSettings.getInstance(project)
        commandLine.addParameter("language-server")
        if (settings.regalVerboseLogging) {
            commandLine.addParameter("--verbose")
        }
        commandLine.workDirectory = project.basePath?.let { java.io.File(it) }

        super.setCommandLine(commandLine)
    }

    private fun showRegalNotFoundNotification() {
        val notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup("OPA Plugin")

        if (notificationGroup != null) {
            val notification = notificationGroup.createNotification(
                "Regal Language Server Not Found",
                "Regal binary not found. Install Regal to enable advanced IDE features like code completion and diagnostics.<br/>" +
                        "<a href=\"https://github.com/StyraInc/regal/releases\">Install Regal</a> or configure the path in Settings → Languages & Frameworks → Open Policy Agent",
                NotificationType.WARNING
            ).setImportant(true)

            notification.notify(project)
        }
    }

    override fun getInitializationOptions(rootUri: VirtualFile?): Any? {
        // Placeholder for future client feature customization (e.g. inline eval, etc.)
        return emptyMap<String, Any>()
    }
}
