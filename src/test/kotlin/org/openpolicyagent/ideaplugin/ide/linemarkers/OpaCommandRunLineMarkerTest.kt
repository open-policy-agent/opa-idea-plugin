/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.linemarkers


class OpaCommandRunLineMarkerTest : OpaLineMarkerProviderTestBase() {

    fun `test 'eval' and 'test' markers on rego file`() = doTestByText("main.rego","""
       package main # - eval or test package
        hello { # - eval rule
            m == "world"
        }
        
        warn[msg]{ # - eval rule
            a == b
        }
        
        test_main { # - test rule
            a==b
        }
    """)

    fun `test 'eval' and 'test' markers also work on rego file with name start with 'test_' issue 47`() = doTestByText("test_main.rego" , """
        package test.main # - eval or test package
        test_main { # - test rule
            a==b
        }
        
        test_main_2 { # - test rule
            a==b
        }
        
        hello { # - eval rule
            m == "world"
        }
    """.trimIndent())

}