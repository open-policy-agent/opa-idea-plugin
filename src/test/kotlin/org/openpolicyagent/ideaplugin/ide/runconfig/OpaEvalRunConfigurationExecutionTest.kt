/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig

import org.assertj.core.api.Assertions.assertThat
import java.nio.file.Paths

class OpaEvalRunConfigurationExecutionTest : RunConfigurationTestBase() {

    fun `test execution with bundle dir`() {
        buildTestProject()
        val config = createConfiguration(
            "data.main.allow",
            Paths.get("${myFixture.tempDirPath}/src/input.json"),
            Paths.get("${myFixture.tempDirPath}/src"),
            "--v0-compatible"
        )
        val out = executeAndGetOutput(config)
        assertThat(out.stdout).contains(
            """
            {
              "result": [
                {
                  "expressions": [
                    {
                      "value": [
                        "allowed by sec"
                      ],
                      "text": "data.main.allow",
                      "location": {
                        "row": 1,
                        "col": 1
                      }
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
        )
        assertThat(out.exitCode).isEqualTo(0)
    }

    fun `test execution with bundle dir and additional args`() {
        buildTestProject()
        val config = createConfiguration(
            "data.main.allow",
            Paths.get("${myFixture.tempDirPath}/src/input.json"),
            Paths.get("${myFixture.tempDirPath}/src"),
            "-f pretty --v0-compatible"
        )
        val out = executeAndGetOutput(config)
        assertThat(out.stdout).contains(
            """
            [
              "allowed by sec"
            ]
        """.trimIndent()
        )
        assertThat(out.exitCode).isEqualTo(0)
    }

    fun `test execution with no bundle and data args positioned in additional args`() {
        buildProject {
            dir("src") {
                rego(
                    "all.rego", """
                        package main

                        allow[msg] {
                            msg:= "allowed by sec"
                        }
                    """.trimIndent()
                )
                rego(
                    "sec.rego", """
                        # we create an invalid file (create a parsing error, to be sure its not load (ie to be sure that bundle is disable
                        package
                    """.trimIndent()
                )

                json(
                    "input.json", """
                        {
                            "sec": true
                        }
                    """.trimIndent()
                )
            }
        }

        val config = createConfiguration(
            "data.main.allow",
            Paths.get("${myFixture.tempDirPath}/src/input.json"),
            null,
            "-d ${myFixture.tempDirPath}/src/all.rego --v0-compatible"
        )
        val out = executeAndGetOutput(config)
        assertThat(out.stdout).contains(
            """
            {
              "result": [
                {
                  "expressions": [
                    {
                      "value": [
                        "allowed by sec"
                      ],
                      "text": "data.main.allow",
                      "location": {
                        "row": 1,
                        "col": 1
                      }
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
        )
        assertThat(out.exitCode).isEqualTo(0)
    }

    private fun buildTestProject() {
        buildProject {
            dir("src") {
                rego(
                    "all.rego", """
                        package main
                        import data.sec

                        allow[msg] {
                            # using a rule define din another package in order to be sure that bundle has load file
                            sec.allow
                            msg:= "allowed by sec"
                        }
                    """.trimIndent()
                )
                rego(
                    "sec.rego", """
                        package sec

                        allow {
                            input.sec == true
                        }
                    """.trimIndent()
                )

                json(
                    "input.json", """
                        {
                            "sec": true
                        }
                    """.trimIndent()
                )
            }
        }
    }
}
