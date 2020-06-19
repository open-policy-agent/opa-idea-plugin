/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang

import com.intellij.testFramework.ParsingTestCase
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.OpaTestCase
import org.openpolicyagent.ideaplugin.OpaTestCase.Companion.testResourcesPath
import org.openpolicyagent.ideaplugin.lang.parser.RegoParserDefinition


abstract class RegoParsingTestCaseBase() : ParsingTestCase(
    "org/openpolicyagent/ideaplugin/lang/parser/fixtures",
    "rego",
    true,
    RegoParserDefinition()
), OpaTestCase {

    override fun getTestDataPath(): String = testResourcesPath

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return OpaTestBase.camelOrWordsToSnake(camelCase)
    }

    /**
     * Test that parsing does not return error
     *
     * This test is very basic, it does not check whether the generated PSI matches the expected one.
     * At this time the grammar / psi is not finish; this test ensure no regressions are introduce when modifying grammar
     */
    fun doTestNoError() {
        super.doTest(false, false)
        ensureNoErrorElements()
    }
}
