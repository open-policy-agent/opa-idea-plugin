/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.test

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.ui.UIUtil
import org.assertj.core.api.Assertions.assertThat
import org.openpolicyagent.ideaplugin.OpaTestCase
import org.openpolicyagent.ideaplugin.ide.runconfig.OpaConfigurationFactory
import org.openpolicyagent.ideaplugin.ide.runconfig.RunConfigurationTestBase
import java.nio.file.Path
import java.nio.file.Paths

abstract class OpaTestRunConfigurationBase : RunConfigurationTestBase() {

    fun createTestConfig(bundleDir: Path? = null, additionalArgs: String? = null): OpaTestRunConfiguration {
        val runConfig = OpaConfigurationFactory(OpaTestRunConfigurationType())
            .createTemplateConfiguration(myFixture.project) as OpaTestRunConfiguration

        runConfig.bundleDir = bundleDir
        runConfig.additionalArgs = additionalArgs
        return runConfig
    }

    /**
     * Run the test configuration and return the root of the testTree.
     *
     * see [checkTreeErrorMsg] and [getFormattedTestTree] for possible assertions
     */
    protected fun executeAndGetTestRoot(configuration: RunConfiguration): SMTestProxy.SMRootTestProxy {
        val result = execute(configuration)
        val executionConsole = result.executionConsole as SMTRunnerConsoleView
        val testsRootNode = executionConsole.resultsViewer.testsRootNode
        with(result.processHandler) {
            startNotify()
            waitFor()
        }
        UIUtil.dispatchAllInvocationEvents()
        Disposer.dispose(executionConsole)
        return testsRootNode
    }

    /**
     * return the test tree as string for easy comparison with an expected output
     * ( because it's a string, Intellij can generate a diff view if the test fails).
     *
     * Example:
     * [root](-)
     * .data.test.main.test_rule_2_should_be_ko(-)
     * .data.test.main.test_should_raise_error(-)
     * .data.test.main.test_rule1_should_be_ok(+)
     *
     * This method has been borrowed to IntelliJ rust project
     */
    protected fun getFormattedTestTree(testTreeRoot: SMTestProxy.SMRootTestProxy) =
        buildString {
            if (testTreeRoot.wasTerminated()) {
                append("Test terminated")
                return@buildString
            }
            formatLevel(testTreeRoot)
        }

    private fun StringBuilder.formatLevel(test: SMTestProxy, level: Int = 0) {
        append(".".repeat(level))
        append(test.name)
        when {
            test.wasTerminated() -> append("[T]")
            test.isPassed -> append("(+)")
            test.isIgnored -> append("(~)")
            else -> append("(-)")
        }

        for (child in test.children) {
            append('\n')
            formatLevel(child, level + 1)
        }
    }

    /**
     * check that the error message of each node matches the desired pattern
     * the error message should correspond to the key "message" in [org.openpolicyagent.ideaplugin.ide.runconfig.test.OpaTestEventsConverter.fireFailedTest])
     *
     * Desired patterns in file named "${node.name}.regex" are stored in the folder at "src/test/resources/${dataPath}/{testName}"
     *
     * eg. [org.openpolicyagent.ideaplugin.ide.runconfig.test.TestRunConfigurationExecutionOpaTest]
     *
     */
    protected fun checkTreeErrorMsg(root: SMTestProxy) {

        for (node in root.children) {
            val pattern =
                FileUtil.loadFile(
                    Paths.get("${OpaTestCase.testResourcesPath}/${dataPath}/${testName}/${node.name}.regex").toFile())

            assertThat(node.errorMessage ?: "")
                .describedAs("test node '${node.name}' does not contain the expected error message")
                .matches(Regex(pattern, RegexOption.MULTILINE).toPattern())
        }

    }

    /**
     * name of the test currently being executed
     */
    private val testName: String
        get() = camelOrWordsToSnake(getTestName(true))

    companion object {
        @JvmStatic
        fun camelOrWordsToSnake(name: String): String {
            if (' ' in name) return name.trim().replace(" ", "_")

            return name.split("(?=[A-Z])".toRegex()).joinToString("_", transform = String::toLowerCase)
        }
    }
}
