/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.runconfig.test

import com.amazon.ion.system.IonTextWriterBuilder.json
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.mock.MockVirtualFile.dir
import org.assertj.core.api.Assertions
import org.jdom.Element
import org.opa.ideaplugin.openapiext.toXmlString
import org.opa.ideaplugin.openapiext.writePath
import org.opa.ideaplugin.openapiext.writeString
import org.opa.ideaplugin.ide.runconfig.test.OpaTestRunConfigurationBase
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

    fun `test read external`() {
        val runConfig = createTestConfig()

        val data = Element("root")
        data.writePath("bundledir", validBundleDir())
        data.writeString("addtionalargs", validAdditionalArgs())

        runConfig.readExternal(data)

        Assertions.assertThat(runConfig.bundleDir).isEqualTo(validBundleDir())
        Assertions.assertThat(runConfig.additionalArgs).isEqualTo(validAdditionalArgs())
    }

    fun `test write external`() {
        val runConfig = createTestConfig()
        runConfig.bundleDir = validBundleDir()
        runConfig.additionalArgs = validAdditionalArgs()

        val elem = Element("root")
        runConfig.writeExternal(elem)


        val expected = Element("root")
        expected.writePath("bundledir", runConfig.bundleDir)
        expected.writeString("addtionalargs", runConfig.additionalArgs)

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

    private companion object {
        const val bundleDirName = "src"
        const val inputFileName = "input.json"
    }

}