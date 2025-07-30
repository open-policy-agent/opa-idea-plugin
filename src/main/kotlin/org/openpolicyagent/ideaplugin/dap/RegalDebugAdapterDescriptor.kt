/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.dap

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.dap.DebugMode
import com.redhat.devtools.lsp4ij.dap.definitions.DebugAdapterServerDefinition
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
import com.redhat.devtools.lsp4ij.dap.descriptors.ServerReadyConfig
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.openpolicyagent.ideaplugin.lang.RegoFileType
import org.openpolicyagent.ideaplugin.opa.project.settings.OpaProjectSettings
import org.openpolicyagent.ideaplugin.util.RegalExecutableUtil
import java.io.File

class RegalDebugAdapterDescriptor(
    @NotNull options: RunConfigurationOptions,
    @NotNull environment: ExecutionEnvironment,
    @Nullable serverDefinition: DebugAdapterServerDefinition?
) : DebugAdapterDescriptor(options, environment, serverDefinition) {

    private val project: Project = environment.project

    companion object {
        private val LOG = Logger.getInstance(RegalDebugAdapterDescriptor::class.java)
    }

    @Throws(ExecutionException::class)
    override fun startServer(): ProcessHandler {
        val commandLine = GeneralCommandLine()
        val regalPath = RegalExecutableUtil.findRegalExecutable(project)

        if (regalPath == null) {
            throw ExecutionException("Regal executable not found. Please install Regal or configure the path in Settings → Languages & Frameworks → Open Policy Agent")
        }

        commandLine.exePath = regalPath
        commandLine.addParameter("debug")

        val settings = OpaProjectSettings.getInstance(project)
        if (settings.regalVerboseLogging) {
            commandLine.addParameter("--verbose")
        }

        commandLine.workDirectory = project.basePath?.let { File(it) }

        LOG.info("Starting Regal debug adapter with command: ${commandLine.commandLineString}")

        return ProcessHandlerFactory.getInstance().createProcessHandler(commandLine)
    }

    override fun getDapParameters(): Map<String, Any> {
        // TODO: Set input and inputPath from project input.json
        return mapOf(
            "command" to "eval",
            "query" to "data",
            "bundlePaths" to listOf(project.basePath ?: "."),
            "input" to emptyMap<String, Any>(),
            "inputPath" to "",
            "stopOnEntry" to true,
            "enablePrint" to true
        )
    }

    override fun getServerReadyConfig(@NotNull debugMode: DebugMode): ServerReadyConfig {
        return ServerReadyConfig(5000) // 5 second timeout
    }

    @Nullable
    override fun getFileType(): FileType? {
        return RegoFileType
    }

}
