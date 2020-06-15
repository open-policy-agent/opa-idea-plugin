/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.lang

class RegoParsingTestCase: RegoParsingTestCaseBase() {

    fun `test array`() = doTestNoError()
    fun `test object`() = doTestNoError()
    fun `test number`() = doTestNoError()

    fun `test array comprehension`() = doTestNoError()
    fun `test set comprehension`() = doTestNoError()
    fun `test object comprehension`() = doTestNoError()

    fun `test implicit or`() = doTestNoError()

    fun `test default keyword`() = doTestNoError()
    fun `test else keyword`() = doTestNoError()
    fun `test with keyword`() = doTestNoError()

    fun `test complex rule 1`() = doTestNoError()
    fun `test complex rule 2`() = doTestNoError()
    fun `test complex rule 3`() = doTestNoError()

}