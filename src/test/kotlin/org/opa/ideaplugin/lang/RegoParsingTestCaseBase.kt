/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.lang

import com.intellij.testFramework.ParsingTestCase
import org.opa.ideaplugin.OpaTestBase
import org.opa.ideaplugin.OpaTestCase
import org.opa.ideaplugin.lang.parser.RegoParserDefinition
import org.opa.ideaplugin.openapiext.execute
import java.util.concurrent.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import org.opa.ideaplugin.opa.tool.OpaBaseTool.Companion.opaBinary


abstract class RegoParsingTestCaseBase() : ParsingTestCase(
    "org/opa/ideaplugin/lang/parser/fixtures",
    "rego",
    true,
    RegoParserDefinition()
), OpaTestCase {

    override fun getTestDataPath(): String = "src/test/resources"

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return OpaTestBase.camelOrWordsToSnake(camelCase)
    }

    /**
     * Test that parsing does not return error
     *
     * This test is very basic bacic, it's does not check the generated psi match the expected one.
     * At this time the grammar / psi is not finish; this test ensure no regressions are introduce when modifying grammar.
     * Also checks rego files by using local opa client with "opa ckeck $filename.rego",
     */
    fun doTestNoError() {
        checkRegoFileForErrorsWithLocalOpaClient()
        super.doTest(false, false)
        ensureNoErrorElements()
    }

    private fun checkRegoFileForErrorsWithLocalOpaClient() {
        try {
            GeneralCommandLine(opaBinary)
                    .withWorkDirectory(super.myFullDataPath)
                    .withParameters("check", "$testName.$myFileExt")
                    .withCharset(Charsets.UTF_8)
                    .execute(project, false)
        } catch (e: ExecutionException) {
            throw e
        }
    }

    fun ignore() {
        super.doTest(false)
    }

    fun doTestNoErrorAndCheckResult() {
        super.doTest(true, false)
        ensureNoErrorElements()
    }
}