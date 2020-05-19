/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin.opa.tool

import com.github.vgramer.opaplugin.OpaTestBase

// TODO : probably need to use a real fs in the future (when opa ftm project and opa eval will implemented)
class OpaFmtTest : OpaTestBase() {

    override val dataPath = "com/github/vgramer/opaplugin/opa/tool/fixtures"

    private fun doTest() {
        myFixture.configureByFile(fileName)
        myFixture.performEditorAction("com.github.vgramer.opaplugin.actions.FmtAction")
        myFixture.checkResultByFile(fileName.replace(".rego", "_after.rego"), true)
    }

    fun `test not formatted`() = doTest()
    fun `test already formatted`() = doTest()

}