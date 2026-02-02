/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.util

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import org.openpolicyagent.ideaplugin.opa.project.settings.OpaProjectSettings
import java.io.File

object RegalExecutableUtil {

    val regalBinary = if (SystemInfo.isWindows) "regal.exe" else "regal"

    fun findRegalExecutable(project: Project): String? {
        val settings = OpaProjectSettings.getInstance(project)
        val configuredPath = settings.regalPath.trim()

        if (configuredPath.isNotEmpty()) {
            val file = File(configuredPath)
            if (file.exists() && file.canExecute()) {
                return configuredPath
            }
        }

        // Try to find regal in PATH
        return PathEnvironmentVariableUtil.findInPath(regalBinary)?.absolutePath
    }
}
