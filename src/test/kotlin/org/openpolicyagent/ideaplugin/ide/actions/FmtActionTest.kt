/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.actions

import org.openpolicyagent.ideaplugin.OpaTestBase

// TODO : probably need to use a real fs in the future (when opa fmt project and opa eval implemented)
class FmtActionTest : OpaTestBase() {
    override val dataPath = "org/openpolicyagent/ideaplugin/ide/actions/fmt/fixtures"

    private fun doTest() {
        myFixture.configureByFile(fileName)
        myFixture.performEditorAction("org.openpolicyagent.ideaplugin.actions.FmtAction")
        myFixture.checkResultByFile(fileName.replace(".rego", "_after.rego"), true)
    }

    fun `test not formatted`() = doTest()
    fun `test already formatted`() = doTest()
}
