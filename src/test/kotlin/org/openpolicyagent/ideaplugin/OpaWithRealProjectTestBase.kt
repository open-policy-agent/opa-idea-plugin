/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase

/**
 * This class creates the Test project in the FS in order to execute OPA commands in tests. The downside is that it
 * is slower than [org.openpolicyagent.ideaplugin.OpaTestBase] which creates the Test project in memory.
 */
abstract class OpaWithRealProjectTestBase : CodeInsightFixtureTestCase<ModuleFixtureBuilder<*>>() {
    open val dataPath: String = ""


    protected val projectDir: VirtualFile get() = myFixture.findFileInTempDir(".")

    protected fun FileTree.create(): TestProject = create(project, projectDir)


    /**
     * create file of the test project.
     * The directory and files are generated in [myFixture.tempDirPath]. So in the following example the file
     * all.rego will be generated in myFixture.tempDirPath}/src/all.rego
     *
     * ```
     * fun `test method`() {
     *     buildProject {
     *         dir("src") {
     *             rego(
     *                 "all.rego", """
     *                         package main
     *
     *                         allow[msg] {
     *                             msg:= "allowed by sec"
     *                         }
     *                     """.trimIndent()
     *             )
     *             json(
     *                 "input.json", """
     *                         {
     *                             "sec": true
     *                         }
     *                     """.trimIndent()
     *             )
     *         }
     *     }
     *
     *     // do some test stuff
     * }
     * ```
     */
    protected fun buildProject(builder: FileTreeBuilder.() -> Unit): TestProject = fileTree { builder() }.create()


}
