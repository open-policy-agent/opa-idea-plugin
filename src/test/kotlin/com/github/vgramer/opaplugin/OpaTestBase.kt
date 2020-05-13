/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin

import com.intellij.openapi.util.io.StreamUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.AssertionFailedError

abstract class OpaTestBase : BasePlatformTestCase(), OpaTestCase {

    open val dataPath: String = ""

    override fun getTestDataPath(): String = "${OpaTestCase.testResourcesPath}/$dataPath"

    protected val fileName: String
        get() = "$testName.rego"

    private val testName: String
        get() = camelOrWordsToSnake(getTestName(true))

    companion object {
        @JvmStatic
        fun camelOrWordsToSnake(name: String): String {
            if (' ' in name) return name.trim().replace(" ", "_")

            return name.split("(?=[A-Z])".toRegex()).joinToString("_", transform = String::toLowerCase)
        }
    }
}