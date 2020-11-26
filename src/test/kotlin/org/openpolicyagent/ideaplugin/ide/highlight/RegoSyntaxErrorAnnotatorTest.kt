/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.highlight

class RegoSyntaxErrorAnnotatorTest : AnnotatorTestBase(RegoSyntaxErrorAnnotator::class){

    fun `test unclosed string is reported as error`(){
        check_error("""
            package main
            a:= <error descr="Missing closing quote">"hello</error>
        """.trimIndent())
    }

    fun `test unclosed string with only one char is reported as error`(){
        check_error("""
            package main
            a:= <error descr="Missing closing quote">"</error>
        """.trimIndent())
    }

    fun `test unclosed string with only escape char is reported as error`(){
        check_error("""
            package main
            a:= <error descr="Missing closing quote">"\"</error>
        """.trimIndent())
    }

    fun `test no error are reported for valid string`(){
        check_error("""
            package main
            a:= "hello world"
        """.trimIndent())
    }

    fun `test no error are reported for valid string containing escape`(){
        check_error("""
            package main
            a:= "hello \”world"
        """.trimIndent())
    }

    fun `test no error are reported for empty string`(){
        check_error("""
            package main
            a:= ""
        """.trimIndent())
    }

    fun `test unclosed raw_string is reported as error`(){
        check_error("""
            package main
            a:= <error descr="Missing closing backtick">`hello</error>
        """.trimIndent())
    }

    fun `test unclosed multi-line raw_string is reported as error`(){
        check_error("""
            package main
            a:= <error descr="Missing closing backtick">`hello
            
            world</error>
        """.trimIndent())
    }

    fun `test unclosed raw_string with only one char is reported as error`(){
        check_error("""
            package main
            a:= <error descr="Missing closing backtick">`</error>
        """.trimIndent())
    }

    fun `test no error are reported for valid raw_string`(){
        check_error("""
            package main
            a:= `hello world`
        """.trimIndent())
    }

    fun `test no error are reported for valid multi-line raw_string`(){
        check_error("""
            package main
            a:= `hello 
            
            world`
        """.trimIndent())
    }

    fun `test no error are reported for empty raw_string`(){
        check_error("""
            package main
            a:= ``
        """.trimIndent())
    }
}