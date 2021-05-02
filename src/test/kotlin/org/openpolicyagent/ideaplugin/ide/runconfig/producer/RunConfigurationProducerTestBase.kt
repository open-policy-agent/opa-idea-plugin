/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// This class is borrow from https://github.com/intellij-rust/intellij-rust/blob/6a4626c5d6cf66050893d10517125fbda77a218c/src/test/kotlin/org/rust/cargo/runconfig/producers/RunConfigurationProducerTestBase.kt#L35
// and adapted to fit the project
package org.openpolicyagent.ideaplugin.ide.runconfig.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiElement
import org.intellij.lang.annotations.Language
import org.jdom.Element
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.lang.psi.ancestorOrSelf
import org.openpolicyagent.ideaplugin.openapiext.toXmlString

abstract class RunConfigurationProducerTestBase : OpaTestBase() {

    override val dataPath = "org/openpolicyagent/ideaplugin/ide/runconfig/producer/fixtures"


    protected fun checkOnLeaf() = checkOnElement<PsiElement>()

    private inline fun <reified T : PsiElement> checkOnElement() {
        var element = myFixture.file.findElementAt(myFixture.caretOffset)
            ?.ancestorOrSelf<T>()
            ?: error("Failed to find element of `${T::class.simpleName}` class at caret")
        val configurationContext = ConfigurationContext(element)
        check(configurationContext)
    }

    protected fun openFileInEditor(path: String) {
        myFixture.configureFromExistingVirtualFile(myFixture.findFileInTempDir(path))
    }


    private fun check(configurationContext: ConfigurationContext) {
        val configurations = configurationContext.configurationsFromContext.orEmpty().map { it.configuration }

        val serialized = configurations.map { config ->
            Element("configuration").apply {
                setAttribute("name", config.name)
                setAttribute("class", config.javaClass.simpleName)
                config.writeExternal(this)
            }
        }

        val root = Element("configurations")
        serialized.forEach { root.addContent(it) }


        assertSameLinesWithFile("$testDataPath/${testName}.xml", root.toXmlString())
    }


    protected fun testProject(description: TestProjectBuilder.() -> Unit) {
        val testProject = TestProjectBuilder()
        testProject.description()
        testProject.build()
    }

    protected inner class TestProjectBuilder {
        private inner class File(
            val path: String,
            val code: String,
            val caretOffset: Int?
        )

        private var files = arrayListOf<File>()
        private var toOpen: File? = null


        fun file(path: String, @Language("rego") code: String): TestProjectBuilder {
            addFile(path, code)
            return this
        }

        fun open(): TestProjectBuilder {
            require(toOpen == null)
            toOpen = files.last()
            return this
        }

        fun build() {
            myFixture.addFileToProject(
                "Cargo.toml", """
                [project]
                name = "test"
                version = 0.0.1
            """
            )
            files.forEach { myFixture.addFileToProject(it.path, it.code) }
            toOpen?.let { toOpen ->
                openFileInEditor(toOpen.path)
                if (toOpen.caretOffset != null) {
                    myFixture.editor.caretModel.moveToOffset(toOpen.caretOffset)
                }
            }

        }


        private fun addFile(path: String, code: String): File {
            val matchResult = Regex("# caret:([0-9]+)").find(code)

            var offset: Int? = null
            if (matchResult != null) {
                val caretOffset = matchResult.groupValues[1].toInt()

                var i = code.indexOf("# caret:")
                while (i > 0 && code[i] != '\n') {
                    i--
                }

                // if i> 0 then code[i] = '\n', so wee add one to get the first char of the line
                val lineStartIdx = if (i > 0) i + 1 else i

                offset = lineStartIdx + caretOffset
            }

            val cleanedCode = code.replace(Regex("# caret:[0-9]+"), "")
            return File(path, cleanedCode, offset).also { files.add(it) }
        }
    }
}