/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.linemarkers


class OpaCommandRunLineMarkerTest : OpaLineMarkerProviderTestBase() {

    fun `test 'eval' markers on rego file`() = doTestByText("main.rego","""
       package main # - eval package
        hello { # - eval rule
            m == "world"
        }
        
        warn[msg]{ # - eval rule
            a == b
        }
    """)

    fun `test 'test' makers on rego test file`() = doTestByText("test_main.rego" , """
        package test.main # - test package
        test_main { # - test rule
            a==b
        }
        
        test_main_2 { # - test rule
            a==b
        }
    """.trimIndent())

}