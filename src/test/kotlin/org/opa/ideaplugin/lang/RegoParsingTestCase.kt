/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.lang

class RegoParsingTestCase: RegoParsingTestCaseBase() {

    fun `test array`() = doTestNoError()
    fun `test object`() = doTestNoError()
    fun `test number`() = doTestNoError()

    fun `test array comprehension`() = doTestNoError()
    fun `test set comprehension`() = doTestNoError()
    fun `test object comprehension`() = ignore() //FIXME

    fun `test implicit or`() = doTestNoError()

    fun `test default keyword`() = doTestNoError()
    fun `test else keyword`() = doTestNoError()
    fun `test with keyword`() = ignore() //FIXME

    fun `test complex rule 1`() = doTestNoError()
    fun `test complex rule 2`() = doTestNoError()
    fun `test complex rule 3`() = ignore() //FIXME


    fun `test array comprehensions 2`() = ignore() //FIXME
    fun `test built in functions`() = ignore() //FIXME
    fun `test functions`() = ignore() //FIXME
    fun `test multiple expressions`() = doTestNoError()
    fun `test rules`() = doTestNoError()
    fun `test self joins`() = doTestNoError()
    fun `test with keyword 2`() = ignore() //FIXME


    fun `test composite keys`() = doTestNoError()
    fun `test composite values`() = doTestNoError()
    fun `test else keyword 2`() = doTestNoError()
    fun `test imports`() = doTestNoError()
    fun `test negations`() = doTestNoError()
    fun `test package with simple rule`() = doTestNoError()
    fun `test references`() = doTestNoError()
    fun `test rules with single predicates`() = doTestNoError()
    fun `test scalars assignment`() = doTestNoError()
    fun `test set comprehensions 2`() = doTestNoError()
    fun `test sets`() = doTestNoError()
    fun `test strings`() = doTestNoError()
    fun `test variables`() = doTestNoError()
}