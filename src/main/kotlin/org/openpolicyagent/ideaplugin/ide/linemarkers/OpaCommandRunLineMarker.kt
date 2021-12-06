/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.linemarkers

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.openpolicyagent.ideaplugin.lang.REGO_TEST_RULE_PREFIX
import org.openpolicyagent.ideaplugin.lang.psi.*


/**
 * Add the marker in the gutter (ie the green arrow) to generate / run  RunConfiguration
 *
 * The RunConfiguration are generated / run by [org.openpolicyagent.ideaplugin.ide.runconfig.producer.OpaEvalRunConfigurationProducer]
 * and [org.openpolicyagent.ideaplugin.ide.runconfig.test.producer.OpaTestRunConfigurationProducer]
 *
 * for more information see [com.intellij.codeInsight.daemon.LineMarkerProvider]
 */
class OpaCommandRunLineMarker : RunLineMarkerContributor() {
    /**
     * returns the marker Information for the [element] if available, null otherwise. if the marker information is null
     * no marker will be created for this [element].
     *
     * We add markers for:
     * - the package "line" in order to generate configuration to evaluate /test the package
     * - the rule "line" in order to generate configuration to evaluate the rule or run this test rule
     *
     * Note: for performance reason marker must be placed on a leaf
     *
     * Implementation note:
     *
     * Marker must be placed on leaf (in our case ASCII_LETTER) so be careful if use PsiTreeUtil.getParentOfType() because
     * a node may contains several ASCII_LETTER leaf. Consequently marker will be created for each leaf.
     *
     * Example:
     * Package node may contains many ASCII_LETTER leafs.
     *
     * for "package test.main" we have the following psi tree
     *
     * RegoPackageImpl(RegoElementType.PACKAGE)
     *      PsiElement(package)
     *      RegoRefImpl(RegoElementType.REF)
     *          RegoVarImpl(RegoElementType.VAR)
     *              PsiElement(ASCII_LETTER) --> test
     *          RegoRefArgImpl(RegoElementType.REF_ARG)
     *              RegoRefArgDotImpl(RegoElementType.REF_ARG_DOT)
     *                  PsiElement(.)
     *                  RegoVarImpl(RegoElementType.VAR)
     *                      PsiElement(ASCII_LETTER) --> main
     *
     *  So if we use PsiTreeUtil.getParentOfType(), we will have 2 markers for "test" and  "main" ASCII_LETTER leaf
     */
    override fun getInfo(element: PsiElement): Info? {
        if (element.containingFile !is RegoFile) return null
        if (element.elementType != RegoTypes.ASCII_LETTER) return null

        if (element.parent.parent.parent is RegoPackage) {
            return Info(AllIcons.RunConfigurations.TestState.Run, { "eval or test package" }, *ExecutorAction.getActions(1))
        }

        if (element.parent.parent is RegoRuleHead) {
            val cmd =  if(element.text.startsWith(REGO_TEST_RULE_PREFIX)) "test"  else "eval"
            return Info(AllIcons.RunConfigurations.TestState.Run, { "$cmd rule" }, *ExecutorAction.getActions(1))
        }

        return null
    }
}