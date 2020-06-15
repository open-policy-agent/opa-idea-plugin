/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// This file has been borrowed from https://github.com/intellij-rust/intellij-rust/blob/master/src/test/kotlin/org/rust/FileTree.kt
// and adapt to fit the project
package org.openpolicyagent.ideaplugin

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil.convertLineSeparators
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.intellij.lang.annotations.Language
import org.openpolicyagent.ideaplugin.openapiext.fullyRefreshDirectory
import org.openpolicyagent.ideaplugin.openapiext.saveAllDocuments
import org.openpolicyagent.ideaplugin.openapiext.toPsiFile
import kotlin.text.Charsets.UTF_8

fun fileTree(builder: FileTreeBuilder.() -> Unit): FileTree =
    FileTree(FileTreeBuilderImpl().apply { builder() }.intoDirectory())

/**
 * Dsl to create directory and file in the test project
 */
interface FileTreeBuilder {
    /**
     * create a directory in the test project
     */
    fun dir(name: String, builder: FileTreeBuilder.() -> Unit)

    /**
     * create a directory in the test project
     */
    fun dir(name: String, tree: FileTree)

    /**
     * create a file in the test project
     */
    fun file(name: String, code: String)

    /**
     * create a rego file. Similar to [file] method but set the language in order to have highlighting in code string
     */
    fun rego(name: String, @Language("Rego") code: String) = file(name, code)

    /**
     * create a json file. Similar to [file] method but set the language in order to have highlighting in code string
     */
    fun json(name: String, @Language("JSON") code: String) = file(name, code)
}

class FileTree(val rootDirectory: Entry.Directory) {
    fun create(project: Project, directory: VirtualFile): TestProject {
        val filesWithCaret: MutableList<String> = mutableListOf()

        fun go(dir: Entry.Directory, root: VirtualFile, parentComponents: List<String> = emptyList()) {
            for ((name, entry) in dir.children) {
                val components = parentComponents + name
                when (entry) {
                    is Entry.File -> {
                        val vFile = root.findChild(name) ?: root.createChildData(root, name)
                        VfsUtil.saveText(vFile, replaceCaretMarker(entry.text))
                        if (hasCaretMarker(entry.text) || "//^" in entry.text || "#^" in entry.text) {
                            filesWithCaret += components.joinToString(separator = "/")
                        }
                    }
                    is Entry.Directory -> {
                        go(entry, root.createChildDirectory(root, name), components)
                    }
                }
            }
        }

        runWriteAction {
            go(rootDirectory, directory)
            fullyRefreshDirectory(directory)
        }

        return TestProject(project, directory, filesWithCaret)
    }

    fun assertEquals(baseDir: VirtualFile) {
        fun go(expected: Entry.Directory, actual: VirtualFile) {
            val actualChildren = actual.children.associateBy { it.name }
            check(expected.children.keys == actualChildren.keys) {
                "Mismatch in directory ${actual.path}\n" +
                        "Expected: ${expected.children.keys}\n" +
                        "Actual  : ${actualChildren.keys}"
            }

            for ((name, entry) in expected.children) {
                val a = actualChildren[name]!!
                when (entry) {
                    is Entry.File -> {
                        check(!a.isDirectory)
                        val actualText = convertLineSeparators(String(a.contentsToByteArray(), UTF_8))
                        check(entry.text.trimEnd() == actualText.trimEnd()) {
                            "Expected:\n${entry.text}\nGot:\n$actualText"
                        }
                    }
                    is Entry.Directory -> go(entry, a)
                }
            }
        }

        saveAllDocuments()
        go(rootDirectory, baseDir)
    }

    fun check(fixture: CodeInsightTestFixture) {
        fun go(dir: Entry.Directory, rootPath: String) {
            for ((name, entry) in dir.children) {
                val path = "${rootPath}/${name}"
                when (entry) {
                    is Entry.File -> fixture.checkResult(path, entry.text, true)
                    is Entry.Directory -> go(entry, path)
                }
            }
        }

        go(rootDirectory, ".")
    }
}

class TestProject(
    private val project: Project,
    val root: VirtualFile,
    private val filesWithCaret: List<String>
) {

    val fileWithCaret: String get() = filesWithCaret.singleOrNull()!!


    fun doFindElementInFile(path: String): PsiElement {
        val vFile = root.findFileByRelativePath(path)
            ?: error("No `$path` file in test project")
        val file = vFile.toPsiFile(project)!!
        return findElementInFile(file, "^")
    }

    fun psiFile(path: String): PsiFileSystemItem {
        val vFile = root.findFileByRelativePath(path)
            ?: error("Can't find `$path`")
        val psiManager = PsiManager.getInstance(project)
        return if (vFile.isDirectory) psiManager.findDirectory(vFile)!! else psiManager.findFile(vFile)!!
    }
}


private class FileTreeBuilderImpl(val directory: MutableMap<String, Entry> = mutableMapOf()) : FileTreeBuilder {
    override fun dir(name: String, builder: FileTreeBuilder.() -> Unit) {
        check('/' !in name) { "Bad directory name `$name`" }
        directory[name] = FileTreeBuilderImpl().apply { builder() }.intoDirectory()
    }

    override fun dir(name: String, tree: FileTree) {
        check('/' !in name) { "Bad directory name `$name`" }
        directory[name] = tree.rootDirectory
    }

    override fun file(name: String, code: String) {
        check('/' !in name && '.' in name) { "Bad file name `$name`" }
        directory[name] = Entry.File(code.trimIndent())
    }

    fun intoDirectory() = Entry.Directory(directory)
}

sealed class Entry {
    class File(val text: String) : Entry()
    class Directory(val children: MutableMap<String, Entry>) : Entry()
}

private fun findElementInFile(file: PsiFile, marker: String): PsiElement {
    val markerOffset = file.text.indexOf(marker)
    check(markerOffset != -1) { "No `$marker` in \n${file.text}" }

    val doc = PsiDocumentManager.getInstance(file.project).getDocument(file)!!
    val markerLine = doc.getLineNumber(markerOffset)
    val makerColumn = markerOffset - doc.getLineStartOffset(markerLine)
    val elementOffset = doc.getLineStartOffset(markerLine - 1) + makerColumn

    return file.findElementAt(elementOffset) ?: error { "No element found, offset = $elementOffset" }
}

fun replaceCaretMarker(text: String): String = text.replace("# caret", "<caret>")
fun hasCaretMarker(text: String): Boolean = text.contains("# caret")
