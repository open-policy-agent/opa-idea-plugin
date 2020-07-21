/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.test

import org.openpolicyagent.ideaplugin.ide.runconfig.producer.RunConfigurationProducerTestBase


class OpaTestRunConfigurationProducerTest : RunConfigurationProducerTestBase() {
    fun `test test producer should generate config for test rule `() {

        testProject {
            file(
                "test_main.rego", """
                    package test.main.abc.efg

                    test_1 { # caret:0
                        1 == 1
                    }

                """.trimIndent()
            ).open()

        }
        checkOnLeaf()
    }

    fun `test test producer should generate config to test package`() {

        testProject {
            file(
                "test_main.rego", """
                    package main.abc.efg # caret:0
                    
                    test_ rule_1 { 
                        input.a == "b"
                    }
                """.trimIndent()
            ).open()

        }
        checkOnLeaf()
    }
}