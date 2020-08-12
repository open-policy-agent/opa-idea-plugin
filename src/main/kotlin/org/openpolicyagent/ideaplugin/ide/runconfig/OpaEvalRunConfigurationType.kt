/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package org.openpolicyagent.ideaplugin.ide.runconfig

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import org.openpolicyagent.ideaplugin.lang.RegoIcons
import javax.swing.Icon

/**
 * ConfigurationType represent an entry in the 'Add new configuration' menu
 *
 * @link https://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_management.html
 */
class OpaEvalRunConfigurationType : ConfigurationType {
    override fun getId(): String = "OPA_RUN_CONFIGURATION"
    override fun getDisplayName(): String = "Opa eval"
    override fun getConfigurationTypeDescription(): String = "Opa eval"
    override fun getIcon(): Icon = RegoIcons.OPA
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(OpaConfigurationFactory(this))

    companion object {
        fun getInstance(): OpaEvalRunConfigurationType =
            ConfigurationTypeUtil.findConfigurationType(OpaEvalRunConfigurationType::class.java)
    }
}

