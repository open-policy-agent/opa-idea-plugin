/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import org.openpolicyagent.ideaplugin.ide.runconfig.OpaConfigurationFactory
import org.openpolicyagent.ideaplugin.ide.runconfig.OpaEvalRunConfiguration
import org.openpolicyagent.ideaplugin.ide.runconfig.OpaEvalRunConfigurationType
import org.openpolicyagent.ideaplugin.lang.REGO_TEST_RULE_PREFIX
import org.openpolicyagent.ideaplugin.lang.psi.*

class OpaEvalRunConfigurationProducer : LazyRunConfigurationProducer<OpaEvalRunConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory {
        return OpaConfigurationFactory(OpaEvalRunConfigurationType.getInstance())
    }

    override fun isConfigurationFromContext(
        configuration: OpaEvalRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val element = context.psiLocation
        val regoPackage = element?.getRegoPackage() ?: return false

        val (query, _) = queryAndOptions(element, regoPackage)
        return configuration.query == query
    }

    override fun setupConfigurationFromContext(
        configuration: OpaEvalRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val regFile = context.location?.virtualFile?.isRegoFile ?:false
        if (!regFile) return false

        val element = sourceElement.get()
        if (element.parent?.parent is RegoRuleHead && element.text.startsWith(REGO_TEST_RULE_PREFIX)) return false
        val regoPackage = element.getRegoPackage() ?: return false


        val (query, options) = queryAndOptions(element, regoPackage)

        configuration.query = query
        configuration.additionalArgs = "-f pretty $options"
        configuration.name = "eval ${configuration.query!!}"
        return true
    }

    /**
     * generate the query and option(as a Pair) to pass to `opa eval` command according to the PsiElement [element]. It can generate
     * 2 types of queries
     * <ul>
     *     <li> eval for package with metrics enable (eg Pair<data.main,  --metrics>)
     *     <li> eval a rule in a package (eg Pair<data.main.rule1, "">)
     * </ul>
     */
    private fun queryAndOptions(element: PsiElement, regoPackage: RegoPackage?): Pair<String, String> {
        if (element.parent.parent is RegoRuleHead) {
            return Pair("data.${regoPackage?.ref?.text}.${element.text}", "")
        }
        return Pair("data.${regoPackage?.ref?.text}", "--metrics")
    }
}