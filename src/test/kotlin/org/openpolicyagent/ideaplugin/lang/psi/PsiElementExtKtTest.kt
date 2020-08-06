/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang.psi

import com.intellij.json.JsonFileType
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.lang.RegoFileType
import org.assertj.core.api.Assertions.assertThat

class PsiElementExtKtTest: OpaTestBase() {

    fun `test getRegoPackage returns null if file is not a rego file`(){
        val file = myFixture.configureByText(JsonFileType(),"""
            {"a": "b"}
        """.trimIndent())

        assertThat(file.lastChild.getRegoPackage()).isNull()

    }

    fun `test getRegoPackage returns null if file does not contains a rego package`(){
        val file = myFixture.configureByText(RegoFileType,"""
            rule_1{
                1 == 2
            }
        """.trimIndent())

        assertThat(file.lastChild.getRegoPackage()).isNull()
    }

    fun `test getRegoPackage returns the rego package if file is rego file and contains a package`(){
        val file = myFixture.configureByText(RegoFileType,"""
            package main
            
            rule_1{
                1 == 2
            }
        """.trimIndent())

        assertThat(file.lastChild.getRegoPackage()).isInstanceOf(RegoPackage::class.java)
    }
}