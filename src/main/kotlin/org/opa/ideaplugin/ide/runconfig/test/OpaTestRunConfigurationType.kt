/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.runconfig.test

import org.opa.ideaplugin.ide.runconfig.OpaConfigurationFactory
import org.opa.ideaplugin.lang.RegoIcons
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import javax.swing.Icon

/**
 * ConfigurationType represent an entry in the 'Add new configuration' menu
 *
 * @link https://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_management.html
 */
class OpaTestRunConfigurationType : ConfigurationType {
    override fun getId(): String = "OPA_TEST_RUN_CONFIGURATION"
    override fun getDisplayName(): String = "Opa test"
    override fun getConfigurationTypeDescription(): String = "Opa test"
    override fun getIcon(): Icon = RegoIcons.OPA
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(OpaConfigurationFactory(this))
}

