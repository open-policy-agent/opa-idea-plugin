/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin.ide.runconfig

import com.github.vgramer.opaplugin.OpaWithRealProjectTestBase
import com.intellij.execution.ExecutionResult
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
import java.nio.file.Path

// TODO probably generalize this class when add more runConfiguration...
abstract class RunConfigurationTestBase : OpaWithRealProjectTestBase() {
    protected fun createConfiguration(
        query: String?,
        input: Path?,
        bundleDir: Path?,
        additionalArgs: String?
    ): OpaEvalRunConfiguration {
        val runConfig = OpaConfigurationFactory(OpaEvalRunConfigurationType())
            .createTemplateConfiguration(myFixture.project)

        runConfig.query = query
        runConfig.bundleDir = bundleDir
        runConfig.input = input
        runConfig.additionalArgs = additionalArgs


        return runConfig
    }


    protected fun execute(configuration: RunConfiguration): ExecutionResult {
        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val state = ExecutionEnvironmentBuilder
            .create(executor, configuration)
            .build()
            .state!!
        return state.execute(executor, ProgramRunner.getRunner(executor.id, configuration)!!)!!
    }

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
