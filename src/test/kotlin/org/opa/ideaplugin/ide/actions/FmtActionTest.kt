/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.actions

import org.opa.ideaplugin.OpaTestBase

// TODO : probably need to use a real fs in the future (when opa ftm project and opa eval will implemented)
class FmtActionTest : OpaTestBase() {

    override val dataPath = "org/opa/ideaplugin/ide/actions/fmt/fixtures"

    private fun doTest() {
        myFixture.configureByFile(fileName)
        myFixture.performEditorAction("org.opa.ideaplugin.actions.FmtAction")
        myFixture.checkResultByFile(fileName.replace(".rego", "_after.rego"), true)
    }

    fun `test not formatted`() = doTest()
    fun `test already formatted`() = doTest()

}