/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.test

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.RuntimeConfigurationError
import org.assertj.core.api.Assertions
import org.jdom.Element
import org.openpolicyagent.ideaplugin.openapiext.toXmlString
import org.openpolicyagent.ideaplugin.openapiext.writePath
import org.openpolicyagent.ideaplugin.openapiext.writeString
import java.nio.file.Path
import java.nio.file.Paths

class OpaTestRunConfigurationTest : OpaTestRunConfigurationBase() {

    fun `test valid config, bundle and additionalArgs)`() {
        val config = buildProjectAndGetRunConfig(validBundleDir(), validAdditionalArgs())
        // Should not raise exception to pass the test
        config.checkConfiguration()
    }

    fun `test valid config, bundle=ok and additionalArg without format`() {
        val config = buildProjectAndGetRunConfig(validBundleDir(), "-v")
        // Should not raise exception to pass the test
        config.checkConfiguration()
    }

    fun `test valid config, bundle=ok and additionalArg without verbose`() {
        val config = buildProjectAndGetRunConfig(validBundleDir(), "-f pretty")
        // Should not raise exception to pass the test
        config.checkConfiguration()
    }

    fun `test valid config (bundle and additionalArgs without format and verbose)`() {
        val config = buildProjectAndGetRunConfig(validBundleDir(), "")
        // Should not raise exception to pass the test
        config.checkConfiguration()
    }

    fun `test invalid config format(-f) is not equals to pretty`() = doCheckConfigError(
        validBundleDir(),
        "-f json",
        "Only format option (-f or --format) = pretty is handle by plugin"
    )

    fun `test invalid config format(--format) is not equals to pretty`() = doCheckConfigError(
        validBundleDir(),
        "--format json",
        "Only format option (-f or --format) = pretty is handle by plugin"
    )

    fun `test invalid config, bundle is a file`() = doCheckConfigError(
        Paths.get("${myFixture.tempDirPath}/${bundleDirName}/${inputFileName}"),
        validAdditionalArgs(),
        "Bundle directory must be a directory"
    )

    fun `test invalid config, bundle is null`() = doCheckConfigError(
        null,
        validAdditionalArgs(),
        "Bundle directory must be a directory"
    )

    fun `test read external`() {
        val runConfig = createTestConfig()
        runConfig.env = validEnvs()

        val data = Element("root")
        data.writePath("bundledir", validBundleDir())
        data.writeString("additionalargs", validAdditionalArgs())
        runConfig.env.writeExternal(data)

        runConfig.readExternal(data)

        Assertions.assertThat(runConfig.bundleDir).isEqualTo(validBundleDir())
        Assertions.assertThat(runConfig.additionalArgs).isEqualTo(validAdditionalArgs())
        Assertions.assertThat(runConfig.env).isEqualTo(validEnvs())
    }

    fun `test write external`() {
        val runConfig = createTestConfig()
        runConfig.bundleDir = validBundleDir()
        runConfig.additionalArgs = validAdditionalArgs()
        runConfig.env = validEnvs()

        val elem = Element("root")
        runConfig.writeExternal(elem)


        val expected = Element("root")
        expected.writePath("bundledir", runConfig.bundleDir)
        expected.writeString("additionalargs", runConfig.additionalArgs)
        runConfig.env.writeExternal(expected)

        Assertions.assertThat(elem.toXmlString()).isEqualTo(expected.toXmlString())

    }


    private fun doCheckConfigError(
        bundleDir: Path?,
        additionalArgs: String?,
        errorMsg: String
    ) {

        val runConfig = buildProjectAndGetRunConfig(bundleDir, additionalArgs)

        try {
            runConfig.checkConfiguration()
            Assertions.failBecauseExceptionWasNotThrown<RuntimeConfigurationError>(RuntimeConfigurationError::class.java)
        } catch (e: RuntimeConfigurationError) {
            Assertions.assertThat(e).hasMessage(errorMsg)
        }
    }


    private fun buildProjectAndGetRunConfig(
        bundleDir: Path?,
        additionalArgs: String?
    ): OpaTestRunConfiguration {

        buildProject {
            dir(bundleDirName) {
                json(
                    inputFileName, """
                        {"a":  "b"}
                    """
                )
            }
        }

        val runConfig = createTestConfig()

        runConfig.bundleDir = bundleDir
        runConfig.additionalArgs = additionalArgs
        return runConfig
    }

    private fun validBundleDir(): Path = Paths.get("${myFixture.tempDirPath}/${bundleDirName}")
    private fun validAdditionalArgs() = "-f pretty -v -t 12s"
    private fun validEnvs() = EnvironmentVariablesData.DEFAULT.with(mapOf("CUSTOM_ENV" to "custom-value"))

    private companion object {
        const val bundleDirName = "src"
        const val inputFileName = "input.json"
    }

}