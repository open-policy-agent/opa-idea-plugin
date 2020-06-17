/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.test

import org.assertj.core.api.Assertions.assertThat
import java.nio.file.Paths

class TestRunConfigurationExecutionOpaTest : OpaTestRunConfigurationBase() {

    override val dataPath = "org/openpolicyagent/ideaplugin/ide/runconfig/test"

    fun `test runner with test pass fail and error`() {
        buildProject {
            dir("src") {
                rego(
                        "main.rego", """
                        package main

                        rule1[msg] {
                            msg := "msg from rule 1"
                        }
                        
                        rule2[msg] {
                            msg := "msg from rule 2"
                        }
                    """.trimIndent()
                )
                rego(
                        "test_main.rego", """
                        package test.main
                        import data.main
                        
                        test_rule1_should_be_ok{
                            msg := main.rule1
                            msg ==  {"msg from rule 1"}
                        }
                        
                        test_rule_2_should_be_ko{
                            msg := main.rule2
                            msg ==  {"another message in order to put the test in FAIL state"}
                        }
                        
                        test_should_raise_error {
                            # will generate an the following error
                            # eval_builtin_error: to_number: strconv.ParseFloat: parsing "x": invalid syntax
                            # so test will be in ERROR state
                            to_number("x")
                        }
                        
                    """.trimIndent()
                )
            }
        }


        val config = createTestConfig(
                Paths.get("${myFixture.tempDirPath}/src"),
                "-f pretty -v"
        )

        val expectedFormattedTestTree = """
            [root](-)
            .data.test.main.test_rule_2_should_be_ko(-)
            .data.test.main.test_rule1_should_be_ok(+)
            .data.test.main.test_should_raise_error(-)
        """
        val root = executeAndGetTestRoot(config)


        assertEquals(expectedFormattedTestTree.trimIndent(), getFormattedTestTree(root))
        checkTreeErrorMsg(root)
        assertThat(root.children).extracting("stacktrace").containsOnlyNulls()
    }

    fun `test runner with test error pass and fail`() {
        buildProject {
            dir("src") {
                rego(
                        "main.rego", """
                        package main

                        rule1[msg] {
                            msg := "msg from rule 1"
                        }
                        
                        rule2[msg] {
                            msg := "msg from rule 2"
                        }
                    """.trimIndent()
                )
                rego(
                        "test_main.rego", """
                        package test.main
                        import data.main
                        
                        test_should_raise_error {
                            # will generate an the following error
                            # eval_builtin_error: to_number: strconv.ParseFloat: parsing "x": invalid syntax
                            # so test will be in ERROR state
                            to_number("x")
                        }
                                                
                        test_rule1_should_be_ok{
                            msg := main.rule1
                            msg ==  {"msg from rule 1"}
                        }
                        
                        test_rule_2_should_be_ko{
                            msg := main.rule2
                            msg ==  {"another message in order to put the test in FAIL state"}
                        }

                    """.trimIndent()
                )
            }
        }

        val config = createTestConfig(
                Paths.get("${myFixture.tempDirPath}/src"),
                "-f pretty -v"
        )

        val expectedFormattedTestTree = """
            [root](-)
            .data.test.main.test_rule_2_should_be_ko(-)
            .data.test.main.test_should_raise_error(-)
            .data.test.main.test_rule1_should_be_ok(+)
        """
        val root = executeAndGetTestRoot(config)


        assertEquals(expectedFormattedTestTree.trimIndent(), getFormattedTestTree(root))
        checkTreeErrorMsg(root)
        assertThat(root.children).extracting("stacktrace").containsOnlyNulls()

    }


    fun `test runner all test error`() {
        buildProject {
            dir("src") {
                rego(
                        "main.rego", """
                        package main

                        rule1[msg] {
                            msg := "msg from rule 1"
                        }
                        
                        rule2[msg] {
                            msg := "msg from rule 2"
                        }
                    """.trimIndent()
                )
                rego(
                        "test_main.rego", """
                        package test.main
                        import data.main
                        
                        test_rule1_should_raise_error{
                            # will generate an the following error
                            # eval_builtin_error: to_number: strconv.ParseFloat: parsing "x": invalid syntax
                            # so test will be in ERROR state
                            to_number("rule1")
                        }
                        
                        test_rule2_should_raise_error{
                            to_number("rule2")
                        }
                        
                        test_3_should_raise_error {
                            to_number("test3")
                        }
                    """.trimIndent()
                )
            }
        }

        val config = createTestConfig(
                Paths.get("${myFixture.tempDirPath}/src"),
                "-f pretty -v"
        )

        val expectedFormattedTestTree = """
            [root](-)
            .data.test.main.test_rule1_should_raise_error(-)
            .data.test.main.test_rule2_should_raise_error(-)
            .data.test.main.test_3_should_raise_error(-)
        """
        val root = executeAndGetTestRoot(config)

        assertEquals(expectedFormattedTestTree.trimIndent(), getFormattedTestTree(root))
        checkTreeErrorMsg(root)
        assertThat(root.children).extracting("stacktrace").containsOnlyNulls()

    }

    fun `test runner with all tests fail`() {
        buildProject {
            dir("src") {
                rego(
                        "main.rego", """
                        package main

                        rule1[msg] {
                            msg := "msg from rule 1"
                        }
                        
                        rule2[msg] {
                            msg := "msg from rule 2"
                        }
                    """.trimIndent()
                )
                rego(
                        "test_main.rego", """
                        package test.main
                        import data.main
                        
                                                                       
                        test_rule1_should_be_ko{
                            msg := main.rule1
                            msg ==  {"should fail test 1"}
                        }
                        
                        test_rule_2_should_be_ko{
                            msg := main.rule2
                            msg ==  {"another message in order to put the test in FAIL state"}
                        }
                        
                        test_should_fail {
                            2 == 1
                        }

                    """.trimIndent()
                )
            }
        }

        val config = createTestConfig(
                Paths.get("${myFixture.tempDirPath}/src"),
                "-f pretty -v"
        )

        val expectedFormattedTestTree = """
            [root](-)
            .data.test.main.test_rule1_should_be_ko(-)
            .data.test.main.test_rule_2_should_be_ko(-)
            .data.test.main.test_should_fail(-)
        """
        val root = executeAndGetTestRoot(config)

        assertEquals(expectedFormattedTestTree.trimIndent(), getFormattedTestTree(root))
        checkTreeErrorMsg(root)
        assertThat(root.children).extracting("stacktrace").containsOnlyNulls()

    }

    fun `test runner all test pass`() {
        buildProject {
            dir("src") {
                rego(
                        "main.rego", """
                        package main

                        rule1[msg] {
                            msg := "msg from rule 1"
                        }
                        
                        rule2[msg] {
                            msg := "msg from rule 2"
                        }
                    """.trimIndent()
                )
                rego(
                        "test_main.rego", """
                        package test.main
                        import data.main
                        
                        test_rule1_should_be_ok{
                            msg := main.rule1
                            msg ==  {"msg from rule 1"}
                        }
                        
                        test_rule_2_should_be_ok{
                            msg := main.rule2
                            msg ==  {"msg from rule 2"}
                        }
                        
                        test_3_should_be_ok {
                            1 == 1
                        }
                    """.trimIndent()
                )
            }
        }

        val config = createTestConfig(
                Paths.get("${myFixture.tempDirPath}/src"),
                "-f pretty -v"
        )

        val expectedFormattedTestTree = """
            [root](+)
            .data.test.main.test_rule1_should_be_ok(+)
            .data.test.main.test_rule_2_should_be_ok(+)
            .data.test.main.test_3_should_be_ok(+)
        """
        val root = executeAndGetTestRoot(config)

        assertEquals(expectedFormattedTestTree.trimIndent(), getFormattedTestTree(root))
        assertThat(root.children).extracting("errorMessage").containsOnlyNulls()
        assertThat(root.children).extracting("stacktrace").containsOnlyNulls()

    }
}