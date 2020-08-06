/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.psi
import com.intellij.json.JsonFileType
import org.assertj.core.api.Assertions.assertThat


import org.openpolicyagent.ideaplugin.OpaTestBase

class PsiFileExtKtTest : OpaTestBase(){
    companion object {
        const val REGO_SOURCE_FILE_NAME = "main.rego"
        val  REGO_SOURCE_FILE_CONTENT = """
            package main
            
            rule_1{
                1 == 1
            }
        """.trimIndent()

        const val REGO_TEST_FILE_NAME = "test_main.rego"
        val  REGO_TEST_FILE_CONTENT = """
            package test.main
            
            test_rule_1{
                1 == 1
            }
        """.trimIndent()

    }

    fun `test isRegoSourceFile returns true when file is rego source file`(){
        val file= myFixture.configureByText( REGO_SOURCE_FILE_NAME, REGO_SOURCE_FILE_CONTENT)
        assertThat(file.isRegoSourceFile()).isTrue()
    }

    fun `test isRegoSourceFile returns false when file is rego test file`(){
        val file= myFixture.configureByText( REGO_TEST_FILE_NAME, REGO_TEST_FILE_CONTENT)
        assertThat(file.isRegoSourceFile()).isFalse()
    }

    fun `test isRegoSourceFile returns false when file is not a Rego file`(){
        val file= myFixture.configureByText(JsonFileType(),"""
            {"a": "b"}
        """.trimIndent())
        assertThat(file.isRegoSourceFile()).isFalse()
    }

    fun `test isRegoTestFile returns true when file is rego test file`(){
        val file= myFixture.configureByText(REGO_TEST_FILE_NAME, REGO_TEST_FILE_CONTENT)
        assertThat(file.isRegoTestFile()).isTrue()
    }

    fun `test isRegoTestFile returns false when file is rego source file`(){
        val file= myFixture.configureByText(REGO_SOURCE_FILE_NAME, REGO_SOURCE_FILE_CONTENT)
        assertThat(file.isRegoTestFile()).isFalse()
    }

    fun `test isRegoTestFile returns false when file is not a Rego file`(){
        val file= myFixture.configureByText(JsonFileType(),"""
            {"a": "b"}
        """.trimIndent())
        assertThat(file.isRegoTestFile()).isFalse()
    }
}