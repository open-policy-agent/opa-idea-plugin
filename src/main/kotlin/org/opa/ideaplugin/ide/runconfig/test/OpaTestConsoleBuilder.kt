/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.runconfig.test

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.ui.ConsoleView
import org.opa.ideaplugin.ide.runconfig.test.OpaTestConsoleProperties.Companion.TEST_FRAMEWORK_NAME

class OpaTestConsoleBuilder(
    private val config: RunConfiguration,
    private val executor: Executor
) : TextConsoleBuilder() {
    private val filters: MutableList<Filter> = mutableListOf()

    /**
     * add Filter to the console
     *
     * Filter allow to make processing on the output of each test. For example make url link clickable...
     */
    override fun addFilter(filter: Filter) {
        filters.add(filter)
    }

    override fun setViewer(isViewer: Boolean) {}

    override fun getConsole(): ConsoleView {
        val consoleProperties = OpaTestConsoleProperties(config, executor)
        val consoleView = SMTestRunnerConnectionUtil.createConsole(TEST_FRAMEWORK_NAME, consoleProperties)
        filters.forEach { consoleView.addMessageFilter(it) }
        return consoleView
    }
}
