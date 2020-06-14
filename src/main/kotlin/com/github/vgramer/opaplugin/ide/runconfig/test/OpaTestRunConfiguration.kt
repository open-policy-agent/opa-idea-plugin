/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.github.vgramer.opaplugin.ide.runconfig.test

import com.github.vgramer.opaplugin.ide.runconfig.ui.OpaTestRunCommandEditor
import com.github.vgramer.opaplugin.openapiext.readPath
import com.github.vgramer.opaplugin.openapiext.readString
import com.github.vgramer.opaplugin.openapiext.writePath
import com.github.vgramer.opaplugin.openapiext.writeString
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import com.intellij.util.io.isFile
import org.jdom.Element
import java.nio.file.Path

/**
 * the opa test configuration
 *
 * we don't use the options mechanism to persist configuration like in tutorial because its create another class a bit
 * painful to maintain and we had some problems on the restore of parameters
 *
 * @link https://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations.html
 */
class OpaTestRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<OpaTestRunProfileState>(project, factory, name) {

    /**
     * the bundle directory to pass to opa eval (ie option -b )
     */
    var bundleDir: Path? = null

    /**
     * others arguments to pass to opa eval command (eg -f pretty)
     */
    var additionalArgs: String? = null


    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> = OpaTestRunCommandEditor(project)


    override fun checkConfiguration() {
        checkConfig()
    }


    private fun checkConfig() {
        val args = ParametersListUtil.parse(additionalArgs ?: "")


        val formatOrdinal = getFormatOptionIndex(args)
        if (formatOrdinal != -1 && formatOrdinal + 1 < args.size && args[formatOrdinal + 1] != "pretty") {
            throw RuntimeConfigurationError("Only format option (-f or --format) = pretty is handle by plugin")
        }

        // TODO check that if bundle dir is null, a path is provided to command
        bundleDir?.let {
            if (it.isFile()) {
                throw RuntimeConfigurationError("Bundle directory must be a directory")
            }
        }
    }

    // TODO may be move logic to another class and pass an already valid configuration( with all necessary options) to
    // [ com.github.vgramer.opaplugin.ide.runconfig.test.OpaTestRunProfileState]
    /**
     * Return the index of the format option (-f or --format) in [args] or -1 if the option is not present
     */
    fun getFormatOptionIndex(args: MutableList<String>): Int {
        val index = args.indexOf("-f")
        if (index == -1) {
            return args.indexOf("--format")
        }
        return index
    }

    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState {
        checkConfig()
        return OpaTestRunProfileState(executionEnvironment, this)
    }

    /**
     * Handle the deserialization of the run configuration (ie read it from .idea/workspace.xml when IDE start or when
     * user want to edit it )
     */
    override fun readExternal(element: Element) {
        super.readExternal(element)

        bundleDir = element.readPath("bundledir")
        additionalArgs = element.readString("addtionalargs")
    }

    /**
     * Handle the serialization of the run configuration (ie save it to .idea/workspace.xml when user modify it and
     * click to apply)
     */
    override fun writeExternal(element: Element) {
        super.writeExternal(element)

        element.writePath("bundledir", bundleDir)
        element.writeString("addtionalargs", additionalArgs)
    }
}

