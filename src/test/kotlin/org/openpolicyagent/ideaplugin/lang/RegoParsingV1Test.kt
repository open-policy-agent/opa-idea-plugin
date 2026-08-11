/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang

import com.intellij.lang.LanguageBraceMatching
import com.intellij.testFramework.ParsingTestCase
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.OpaTestCase.Companion.testResourcesPath
import org.openpolicyagent.ideaplugin.ide.typing.RegoBraceMatcher
import org.openpolicyagent.ideaplugin.lang.parser.RegoParserDefinition

/**
 * Tests that the BNF parser accepts Rego v1 rule-head syntax.
 *
 * Unlike [RegoParsingTestCaseBase], this class does not invoke `opa check` against
 * the fixtures, because that path passes `--v0-compatible` which rejects v1
 * constructs. Instead, the fixtures here are hand-verified valid v1 Rego, and the
 * test asserts only that the BNF parser produces a PSI tree with no error elements.
 */
class RegoParsingV1Test : ParsingTestCase(
    "org/openpolicyagent/ideaplugin/lang/parser/fixtures/v1",
    "rego",
    true,
    RegoParserDefinition()
) {

    override fun setUp() {
        super.setUp()
        addExplicitExtension(LanguageBraceMatching.INSTANCE, RegoLanguage, RegoBraceMatcher())
    }

    override fun getTestDataPath(): String = testResourcesPath

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return OpaTestBase.camelOrWordsToSnake(camelCase)
    }

    private fun doTestNoError() {
        super.doTest(false, false)
        ensureNoErrorElements()
    }

    fun `test general ref heads`() = doTestNoError()
    fun `test if body delimiter`() = doTestNoError()
    fun `test contains partial set`() = doTestNoError()
    fun `test one line bodies`() = doTestNoError()
    fun `test contains identifier uses`() = doTestNoError()
    fun `test every expressions`() = doTestNoError()
    fun `test in operator`() = doTestNoError()
    fun `test some in`() = doTestNoError()
    fun `test keywords as identifiers`() = doTestNoError()
}
