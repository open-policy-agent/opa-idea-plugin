/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang

import com.intellij.lang.LanguageBraceMatching
import com.intellij.testFramework.ParsingTestCase
import com.intellij.util.EnvironmentUtil
import org.apache.commons.io.IOUtils
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.OpaTestCase
import org.openpolicyagent.ideaplugin.OpaTestCase.Companion.testResourcesPath
import org.openpolicyagent.ideaplugin.ide.typing.RegoBraceMatcher
import org.openpolicyagent.ideaplugin.lang.parser.RegoParserDefinition
import org.openpolicyagent.ideaplugin.opa.tool.OpaBaseTool.Companion.opaBinary


abstract class RegoParsingTestCaseBase : ParsingTestCase(
    "org/openpolicyagent/ideaplugin/lang/parser/fixtures",
    "rego",
    true,
    RegoParserDefinition()
), OpaTestCase {

    override fun setUp() {
        super.setUp()
        // fix issue #71
        addExplicitExtension(LanguageBraceMatching.INSTANCE, RegoLanguage, RegoBraceMatcher())
    }

    override fun getTestDataPath(): String = testResourcesPath

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return OpaTestBase.camelOrWordsToSnake(camelCase)
    }

    /**
     * Test that parsing does not return error
     *
     * This test is very basic, it does not check whether the generated PSI matches the expected one.
     * At this time the grammar / psi is not finish; this test ensure no regressions are introduced when modifying grammar.
     * Also checks rego files by using local opa client with "opa check $filename.rego",
     */
    fun doTestNoError() {
        checkRegoFileForErrorsWithLocalOpaClient()
        super.doTest(false, false)
        ensureNoErrorElements()
    }

    /**
     * use opa check command to ensure the file is correct
     *
     * Implementation note:
     * To execute the opa check command, we dont use a GeneralCommandLine like this one
     *
     *  GeneralCommandLine(opaBinary)
     *     .withWorkDirectory(super.myFullDataPath)
     *     .withParameters("check", "$testName.$myFileExt")
     *     .withCharset(Charsets.UTF_8)
     *     .execute(project, false)
     *
     *  because since version 202, the way to load Charset has changed and it's lead to an IllegalStateException:
     *
     *  java.lang.IllegalStateException: @NotNull method com/intellij/openapi/vfs/encoding/EncodingManager.getInstance must not return null
     *  at com.intellij.openapi.vfs.encoding.EncodingManager.$$$reportNull$$$0(EncodingManager.java)
     *  at com.intellij.openapi.vfs.encoding.EncodingManager.getInstance(EncodingManager.java:20)
     *  at com.intellij.execution.configurations.GeneralCommandLine.defaultCharset(GeneralCommandLine.java:124)
     *
     *  I think it's because the ParsingTestCase use a mockApplication instead of CodeInsightTestFixture.
     */
    private fun checkRegoFileForErrorsWithLocalOpaClient() {
        val fileToCheck = "${super.myFullDataPath}/$testName.$myFileExt"

        val pb = ProcessBuilder(opaBinary, "check", fileToCheck, "--v0-compatible")
        val env = pb.environment()
        env.clear()
        env.putAll(EnvironmentUtil.getEnvironmentMap())
        pb.redirectErrorStream(true)

        val p = pb.start()
        p.waitFor()
        if (p.exitValue() != 0) {
            throw Exception(
                """
                opa check fail on file '$fileToCheck'
                output:'${IOUtils.toString(p.inputStream, Charsets.UTF_8)}'
            """.trimIndent()
            )
        }
    }
}
