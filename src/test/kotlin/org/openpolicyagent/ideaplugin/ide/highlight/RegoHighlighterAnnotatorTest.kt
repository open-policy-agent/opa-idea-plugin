/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.highlight


class RegoHighlighterAnnotatorTest : AnnotatorTestBase() {
    fun `test rule name is highlighted`() {
        check_info( """
            package  main
            <HEAD>a</HEAD>:= "hello"
            
            <HEAD>hostnames</HEAD>[name] { name := sites[_].servers[_].hostname }
            
        """.trimIndent())
    }

    fun `test function call is highlighted`() {
        check_info( """
            package  main
            <HEAD>aRule</HEAD> {
                a = <CALL>fun_obj</CALL>(1).a
            }
            
        """.trimIndent())
    }

    fun `test empty set is highlighted`() {
        check_info("""
            package  main
            <HEAD>a</HEAD> := <CALL>set()</CALL>
            
        """.trimIndent())
    }
}