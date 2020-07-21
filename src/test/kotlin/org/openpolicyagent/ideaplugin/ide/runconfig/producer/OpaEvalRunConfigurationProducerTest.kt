/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.producer


class OpaEvalRunConfigurationProducerTest : RunConfigurationProducerTestBase() {

    fun `test eval producer should generate config to eval complex rule`() {

        testProject {
            file(
                "main.rego", """
                    package main.abc.efg

                    rule_1 { # caret:0
                        input.a == "b"
                        1 == 1
                    }
                """.trimIndent()
            ).open()

        }
        checkOnLeaf()
    }

    fun `test eval should generate config to eval eval simple rule`() {
        testProject {
            file(
                "main.rego", """
                    package main.abc.efg
                    
                    api_version_mgs_tmpl = "%s %s - use deprecated apiversion '%s'.This api will be remove in kubernetes v1.16." # caret:0


                    rule_1 { 
                        input.a == "b"
                    }
                """.trimIndent()
            ).open()

        }
        checkOnLeaf()
    }

    fun `test eval producer should generate config to eval package`() {
        testProject {
            file(
                "main.rego", """
                    package main # caret:0

                    rule_1 
                    { 
                        input.a == "b"
                    }
                """.trimIndent()
            ).open()
        }
        checkOnLeaf()
    }
}