/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.dap

import com.intellij.openapi.project.Project
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptorFactory
import com.redhat.devtools.lsp4ij.dap.definitions.DebugAdapterServerDefinition
import com.intellij.execution.configurations.RunConfigurationOptions
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.openpolicyagent.ideaplugin.lang.psi.isRegoFile

class RegalDebugAdapterDescriptorFactory : DebugAdapterDescriptorFactory() {

    override fun getServerDefinition(): DebugAdapterServerDefinition {
        // This will be set by LSP4IJ framework when the factory is registered
        return super.getServerDefinition()
    }

    override fun isDebuggableFile(file: VirtualFile, project: Project): Boolean {
        // Check if file is a Rego file that can be debugged
        return file.isRegoFile
    }

    override fun createDebugAdapterDescriptor(
        @NotNull options: RunConfigurationOptions,
        @NotNull environment: ExecutionEnvironment
    ): DebugAdapterDescriptor {
        return RegalDebugAdapterDescriptor(options, environment, getServerDefinition())
    }
}
