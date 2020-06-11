/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin.ide.runconfig

import com.github.vgramer.opaplugin.openapiext.toXmlString
import com.github.vgramer.opaplugin.openapiext.writePath
import com.github.vgramer.opaplugin.openapiext.writeString
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.jdom.Element
import java.nio.file.Path
import java.nio.file.Paths


class OpaEvalRunConfigurationTest : RunConfigurationTestBase() {
    fun `test checkConfiguration throw exception when query is Null`() =
        doCheckConfigError(null, validInput(), validBundleDir(), validAdditionalArgs(), "Query can not be empty")

    fun `test checkConfiguration throw exception when query is EMPTY`() =
        doCheckConfigError("", validInput(), validBundleDir(), validAdditionalArgs(), "Query can not be empty")

    fun `test checkConfiguration throw exception when input is NULL`() =
        doCheckConfigError(
            validQuery(),
            null,
            validBundleDir(),
            validAdditionalArgs(),
            "Input must be a path to a file"
        )

    fun `test checkConfiguration throw exception when input is EMPTY`() =
        doCheckConfigError(
            validQuery(),
            Paths.get(""),
            validBundleDir(),
            validAdditionalArgs(),
            "Input must be a path to a file"
        )

    fun `test checkConfiguration throw exception when input is a directory`() =
        doCheckConfigError(
            validQuery(),
            validBundleDir(),
            validBundleDir(),
            validAdditionalArgs(),
            "Input must be a path to a file"
        )

    fun `test checkConfiguration throw exception when bundle is NULL and no data arg are defined in additional Args`() =
        doCheckConfigError(
            validQuery(),
            validInput(),
            null,
            validAdditionalArgs(),
            "You must either defined a bundle directory or data through Additional args (option -d <path> or --data <path>)"
        )

    fun `test checkConfiguration throw exception when bundle is EMPTY and no data arg are defined in additional Args`() =
        doCheckConfigError(
            validQuery(),
            validInput(),
            Paths.get(""),
            validAdditionalArgs(),
            "You must either defined a bundle directory or data through Additional args (option -d <path> or --data <path>)"
        )

    fun `test checkConfiguration throw exception when bundle is file`() =
        doCheckConfigError(
            validQuery(),
            validInput(),
            validInput(), //this param is bundleDir
            validAdditionalArgs(),
            "Bundle directory must be a directory"
        )


    fun `test valid configuration (bundle is valid, additional Args has NO data option)`() {
        val runConfig = buildProjectAndGetRunConfig(validQuery(), validInput(), validBundleDir(), "-f pretty")
        // Should not raise exception to pass the test
        runConfig.checkConfiguration()
    }

    fun `test valid configuration (bundle is valid, additional Args has data option)`() {
        val runConfig = buildProjectAndGetRunConfig(validQuery(), validInput(), validBundleDir(), "-d /foo")
        // Should not raise exception to pass the test
        runConfig.checkConfiguration()
    }

    fun `test configuration must be check be call getState`(){
        val runConfig = buildProjectAndGetRunConfig("", validInput(), validBundleDir(), validAdditionalArgs())

        try {
            runConfig.getState(DefaultRunExecutor(), ExecutionEnvironment())
            failBecauseExceptionWasNotThrown<RuntimeConfigurationError>(RuntimeConfigurationError::class.java)
        }catch (e: RuntimeConfigurationError){
            assertThat(e).hasMessage("Query can not be empty")
        }
    }


    fun `test read external`() {
        val runConfig = createConfiguration()

        val data = Element("root")
        data.writeString("query", validQuery())
        data.writePath("input", validInput())
        data.writePath("bundledir", validBundleDir())
        data.writeString("addtionalargs", validAdditionalArgs())

        runConfig.readExternal(data)

        assertThat(runConfig.query).isEqualTo(validQuery())
        assertThat(runConfig.input).isEqualTo(validInput())
        assertThat(runConfig.bundleDir).isEqualTo(validBundleDir())
        assertThat(runConfig.additionalArgs).isEqualTo(validAdditionalArgs())
    }

    fun `test write external`() {
        val runConfig = createConfiguration()

        runConfig.query = validQuery()
        runConfig.bundleDir = validBundleDir()
        runConfig.input = validInput()
        runConfig.additionalArgs = validAdditionalArgs()

        val elem = Element("root")
        runConfig.writeExternal(elem)


        val expected = Element("root")
        expected.writeString("query", runConfig.query)
        expected.writePath("input", runConfig.input)
        expected.writePath("bundledir", runConfig.bundleDir)
        expected.writeString("addtionalargs", runConfig.additionalArgs)

        assertThat(elem.toXmlString()).isEqualTo(expected.toXmlString())

    }

    private fun validQuery() = "count(main.warn) == 2"
    private fun validInput(): Path = Paths.get("${myFixture.tempDirPath}/${bundleDirName}/${inputFileName}")
    private fun validBundleDir(): Path = Paths.get("${myFixture.tempDirPath}/${bundleDirName}")
    private fun validAdditionalArgs() = "-f pretty --fail-defined"

    private fun doCheckConfigError(
        query: String?,
        input: Path?,
        bundleDir: Path?,
        additionalsArgs: String?,
        errorMsg: String
    ) {

        val runConfig = buildProjectAndGetRunConfig(query, input, bundleDir, additionalsArgs)

        try {
            runConfig.checkConfiguration()
            failBecauseExceptionWasNotThrown<RuntimeConfigurationError>(RuntimeConfigurationError::class.java)
        } catch (e: RuntimeConfigurationError) {
            assertThat(e).hasMessage(errorMsg)
        }
    }

    private fun buildProjectAndGetRunConfig(
        query: String?,
        input: Path?,
        bundleDir: Path?,
        additionalsArgs: String?
    ): OpaEvalRunConfiguration {
        buildProject {
            dir(bundleDirName) {
                json(
                    inputFileName, """
                        {"a":  "b"}
                    """
                )
            }
        }


        val runConfig = createConfiguration()

        runConfig.query = query
        runConfig.input = input
        runConfig.bundleDir = bundleDir
        runConfig.additionalArgs = additionalsArgs
        return runConfig
    }


    companion object {
        const val bundleDirName = "src"
        const val inputFileName = "input.json"
    }

}