/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.typing


class RegoQuoteHandlerTest: RegoTypingTestBase() {

    fun `test single quote are note auto closed`()= doTest(
        """
            package main
            a := <caret>
        """.trimIndent(),
        '\'',
        """
            package main
            a := '<caret>
        """.trimIndent()
    )

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  String tests                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun `test quote are auto closed`()= doTest(
        """
            package main
            a := <caret>
        """.trimIndent(),
        '"',
        """
            package main
            a := "<caret>"
        """.trimIndent()
    )

    fun `test when quote is open, typing quote puts caret after closing quote`()= doTest(
        """
            package main
            a := "<caret>
        """.trimIndent(),
        '"',
        """
            package main
            a := ""<caret>
        """.trimIndent()
    )


    fun `test when caret is between quote, typing quote puts caret after closing quote`()= doTest(
        """
            package main
            a := "<caret>"
        """.trimIndent(),
        '"',
        """
            package main
            a := ""<caret>
        """.trimIndent()
    )
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  Raw String tests                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun `test closing backtick`()= doTest(
        """
            package main
            a := <caret>
        """.trimIndent(),
        '`',
        """
            package main
            a := `<caret>`
        """.trimIndent()
    )

    fun `test when backtick is open, typing backtick puts caret after closing backtick`()= doTest(
        """
            package main
            a := `<caret>
        """.trimIndent(),
        '`',
        """
            package main
            a := ``<caret>
        """.trimIndent()
    )


    fun `test when caret is between backtick, typing backtick puts caret after closing backtick`()= doTest(
        """
            package main
            a := `<caret>`
        """.trimIndent(),
        '`',
        """
            package main
            a := ``<caret>
        """.trimIndent()
    )

    fun `test quote are not auto closed inside raw string`()= doTest(
        """
            package main
            a := `<caret>`
        """.trimIndent(),
        '"',
        """
            package main
            a := `"<caret>`
        """.trimIndent()
    )

}