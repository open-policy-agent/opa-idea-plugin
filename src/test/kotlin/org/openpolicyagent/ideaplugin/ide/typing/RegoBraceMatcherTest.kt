/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.typing

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.lang.RegoFileType

class RegoBraceMatcherTest: RegoTypingTestBase() {

    fun `test parenthesis are paired`()= doTest(
        """
            package main
            a := sprinft<caret>
        """.trimIndent(),
        '(',
        """
            package main
            a := sprinft(<caret>)
        """.trimIndent()
    )


    fun `test brackets are paired`()= doTest(
        """
            package main
            a := <caret>
        """.trimIndent(),
        '[',
        """
            package main
            a := [<caret>]
        """.trimIndent()
    )


    fun `test braces are paired`()= doTest(
        """
            package main
            rule_1<caret>
        """.trimIndent(),
        '{',
        """
            package main
            rule_1{<caret>}
        """.trimIndent()
    )

    fun `test parenthesis deletion`()= doTest(
        """
            package main
            a := sprinft(<caret>)
        """.trimIndent(),
        '\b',
        """
            package main
            a := sprinft<caret>
        """.trimIndent()
    )
    fun `test brackets deletion`()= doTest(
        """
            package main
            a := [<caret>]
        """.trimIndent(),
        '\b',
        """
            package main
            a := <caret>
        """.trimIndent()
    )

    fun `test braces deletion`()= doTest(
        """
            package main
            rule_1{<caret>}
        """.trimIndent(),
        '\b',
        """
            package main
            rule_1<caret>
        """.trimIndent()
    )

    fun `test parenthesis match` () = doMatch(
        """
            package main
            a: = sprintf<caret>("hello %v", name[x])
        """.trimIndent(),
        ")"
    )

    fun `test brackets match` () = doMatch(
        """
            package main
            a: = sprintf("hello %v", name<caret>[x])
        """.trimIndent(),
        "]"
    )

    fun `test braces match` () = doMatch(
        """
            package main
            rule_1 <caret>{                
                a: = sprintf("hello %v", name[x])            
            }
        """.trimIndent(),
        "}"
    )


    private fun doMatch(source: String, coBrace: String) {
        myFixture.configureByText(RegoFileType, source)
        val expected = source.replace("<caret>", "").lastIndexOf(coBrace)
        check(BraceMatchingUtil.getMatchedBraceOffset(myFixture.editor, true, myFixture.file) == expected)
    }

}
