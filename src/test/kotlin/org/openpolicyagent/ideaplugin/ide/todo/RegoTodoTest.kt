/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.todo

import com.intellij.editor.TodoItemsTestCase
import org.intellij.lang.annotations.Language
import org.openpolicyagent.ideaplugin.lang.RegoFileType

class RegoTodoTest : TodoItemsTestCase() {
    // TodoItemsTestCase contains tests for  some C style comments which are not compatible with rego. So we disable it
    override fun supportsCStyleMultiLineComments(): Boolean = false
    override fun supportsCStyleSingleLineComments(): Boolean = false
    override fun getFileExtension(): String = RegoFileType.defaultExtension

    fun `test single todo`() = doTest("""
        # [TODO first line]
        # second line
        package main
    """)

    fun `test single toto second line`() = doTest("""
        # first line
        # [TODO second line]
        package main
    """)

    fun `test todo in sentence`() = doTest("""
        # 2020/07/14: [TODO first line]
        # second line
        package main
    """)

    private fun doTest(@Language("rego") text: String) = testTodos(text)

}