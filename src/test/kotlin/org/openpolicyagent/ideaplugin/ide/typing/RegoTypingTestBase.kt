/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.typing

import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.lang.RegoFileType

abstract class RegoTypingTestBase : OpaTestBase() {

    protected fun doTest(before: String, type: Char, after: String) {
        myFixture.configureByText(RegoFileType, before)
        myFixture.type(type)
        myFixture.checkResult(after)
    }
}