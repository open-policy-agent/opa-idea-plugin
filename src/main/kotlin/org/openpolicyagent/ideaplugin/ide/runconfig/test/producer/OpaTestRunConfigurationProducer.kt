/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */


package org.openpolicyagent.ideaplugin.ide.runconfig.test.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.openpolicyagent.ideaplugin.ide.runconfig.OpaConfigurationFactory
import org.openpolicyagent.ideaplugin.ide.runconfig.test.OpaTestRunConfiguration
import org.openpolicyagent.ideaplugin.ide.runconfig.test.OpaTestRunConfigurationType
import org.openpolicyagent.ideaplugin.lang.psi.*
import java.nio.file.Paths

class OpaTestRunConfigurationProducer : LazyRunConfigurationProducer<OpaTestRunConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory {
        return OpaConfigurationFactory(OpaTestRunConfigurationType.getInstance())
    }

    override fun isConfigurationFromContext(
        configuration: OpaTestRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val element = context.psiLocation
        val regoPackage = element?.getRegoPackage() ?: return false

        return configuration.additionalArgs?.matches(Regex(".*${getRunOption(element, regoPackage)}( .*|\$)")) ?: false
    }

    override fun setupConfigurationFromContext(
        configuration: OpaTestRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val element = sourceElement.get()
        if (! element.containingFile.isRegoTestFile()) return false
        val regoPackage = element.getRegoPackage() ?: return false


        configuration.additionalArgs = "-f pretty  ${getRunOption(element, regoPackage)}"
        configuration.name = "test ${element.text}"
        return true
    }


    /**
     * generate the run option to pass to opa eval. This option allow to filter the test to execute by passing a regex
     * matching the test names.
     */
    private fun getRunOption(element: PsiElement, regoPackage: RegoPackage?): String {
        if (element.parent.parent is RegoRuleHead) {
            return "-r data.${regoPackage?.ref?.text}.${element.text}"
        }
        return "-r data.${regoPackage?.ref?.text}"
    }
}