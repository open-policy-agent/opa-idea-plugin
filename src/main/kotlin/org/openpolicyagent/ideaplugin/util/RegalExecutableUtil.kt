/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.util

import com.intellij.openapi.project.Project
import org.openpolicyagent.ideaplugin.opa.project.settings.OpaProjectSettings
import java.io.File

object RegalExecutableUtil {

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
        val pathExecutable = findExecutableInPath("regal")
        if (pathExecutable != null) {
            return pathExecutable
        }

        return null
    }

    private fun findExecutableInPath(executable: String): String? {
        val pathEnv = System.getenv("PATH") ?: return null
        val pathSeparator = System.getProperty("path.separator")

        for (dir in pathEnv.split(pathSeparator)) {
            val file = File(dir, executable)
            if (file.exists() && file.canExecute()) {
                return file.absolutePath
            }
        }

        return null
    }
}
