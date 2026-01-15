/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.dap.DebugAdapterManager

abstract class OpaTestBase : BasePlatformTestCase(), OpaTestCase {

    override fun setUp() {
        super.setUp()

        // Light tests use tempFS which is not compatible with LSP/DAP. Disable them for tests.
        LanguageServerManager.getInstance(myFixture.project).stop("regal")

        val debugAdapterManager = DebugAdapterManager.getInstance()
        debugAdapterManager.getDebugAdapterServerById("regal-debug")
            ?.let { debugAdapterManager.removeDebugAdapterServer(it) }
    }

    open val dataPath: String = ""

    override fun getTestDataPath(): String = "${OpaTestCase.testResourcesPath}/${dataPath}"

    protected val fileName: String
        get() = "$testName.rego"

    protected val testName: String
        get() = camelOrWordsToSnake(getTestName(true))

    companion object {
        @JvmStatic
        fun camelOrWordsToSnake(name: String): String {
            if (' ' in name) return name.trim().replace(" ", "_")

            return name.split("(?=[A-Z])".toRegex()).joinToString("_", transform = String::lowercase)
        }
    }

    protected fun FileTree.create(): TestProject =
        create(myFixture.project, myFixture.findFileInTempDir("."))
}