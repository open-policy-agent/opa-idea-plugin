/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig

import com.intellij.execution.ExecutionResult
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import org.openpolicyagent.ideaplugin.OpaWithRealProjectTestBase
import java.nio.file.Path

// TODO probably generalize this class when add more runConfiguration...
abstract class RunConfigurationTestBase : OpaWithRealProjectTestBase() {
    protected fun createConfiguration(
        query: String? = null,
        input: Path? = null,
        bundleDir: Path? = null,
        additionalArgs: String? = null,
        env: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    ): OpaEvalRunConfiguration {
        val runConfig = OpaConfigurationFactory(OpaEvalRunConfigurationType())
            .createTemplateConfiguration(myFixture.project) as OpaEvalRunConfiguration

        runConfig.query = query
        runConfig.bundleDir = bundleDir
        runConfig.input = input
        runConfig.additionalArgs = additionalArgs
        runConfig.env = env


        return runConfig
    }


    /**
     * Utility method to execute a RunConfiguration. Should not be used in test.
     * use [executeAndGetOutput] or [org.openpolicyagent.ideaplugin.ide.runconfig.test.OpaTestRunConfigurationBase.executeAndGetTestRoot]
     * instead
     */
    protected fun execute(configuration: RunConfiguration): ExecutionResult {
        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val state = ExecutionEnvironmentBuilder
            .create(executor, configuration)
            .build()
            .state!!
        return state.execute(executor, ProgramRunner.getRunner(executor.id, configuration)!!)!!
    }

    /**
     * execute the RunConfiguration and return the captured output
     *
     * this method has been borrowed from IntelliJ rust plugin
     */
    protected fun executeAndGetOutput(configuration: RunConfiguration): ProcessOutput {
        val result = execute(configuration)
        val listener = AnsiAwareCapturingProcessAdapter()
        with(result.processHandler) {
            addProcessListener(listener)
            startNotify()
            waitFor()
        }
        Disposer.dispose(result.executionConsole)
        return listener.output
    }
}

/**
 * this class has been borrowed from IntelliJ rust plugin
 */
class AnsiAwareCapturingProcessAdapter : ProcessAdapter() {
    val output = ProcessOutput()

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) =
        if (outputType === ProcessOutputTypes.STDERR) {
            output.appendStderr(event.text)
        } else {
            output.appendStdout(event.text)
        }


    override fun processTerminated(event: ProcessEvent) {
        output.exitCode = event.exitCode
    }
}
