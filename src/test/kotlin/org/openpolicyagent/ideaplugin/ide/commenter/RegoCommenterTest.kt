/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.commenter

import com.intellij.openapi.actionSystem.IdeActions
import org.openpolicyagent.ideaplugin.OpaTestBase

class RegoCommenterTest : OpaTestBase() {
    override val dataPath = "org/openpolicyagent/ideaplugin/ide/commenter/fixtures"

    private fun doTest(actionId: String) {
        myFixture.configureByFile(fileName)
        myFixture.performEditorAction(actionId)
        myFixture.checkResultByFile(fileName.replace(".rego", "_after.rego"), true)
    }

    fun `test single line`() = doTest(IdeActions.ACTION_COMMENT_LINE)
    fun `test uncomment single line`() = doTest(IdeActions.ACTION_COMMENT_LINE)

    fun `test multi lines`() = doTest(IdeActions.ACTION_COMMENT_LINE)
    fun `test uncomment multi lines`() = doTest(IdeActions.ACTION_COMMENT_LINE)

    fun `test indented single line`() = doTest(IdeActions.ACTION_COMMENT_LINE)

    fun `test single line nested comment`() = doTest(IdeActions.ACTION_COMMENT_LINE)
    fun `test uncomment single line nested comment`() = doTest(IdeActions.ACTION_COMMENT_LINE)
}