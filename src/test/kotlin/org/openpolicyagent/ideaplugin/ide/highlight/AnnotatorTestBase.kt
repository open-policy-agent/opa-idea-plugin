/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.highlight

import com.intellij.codeInsight.daemon.impl.SeveritiesProvider
import org.intellij.lang.annotations.Language
import org.openpolicyagent.ideaplugin.OpaTestBase
import org.openpolicyagent.ideaplugin.ide.colors.RegoColor
import kotlin.reflect.KClass

abstract class AnnotatorTestBase(private val annotatorClass: KClass<out AnnotatorBase>): OpaTestBase() {
    /**
     * override setup to add register  RegoColor names has new severities. It allow us to test text is correctly
     * highlighted. for more information see [check_info]
     *
     * Also enable only the annotator [annotatorClass]. For more informationn see [org.openpolicyagent.ideaplugin.ide.highlight.AnnotatorBase]
     */
    override fun setUp() {
        super.setUp()
        AnnotatorBase.enableAnnotator(annotatorClass.java, testRootDisposable)

        val testSeverityProvider = TestSeverityProvider(RegoColor.values().map(RegoColor::testSeverity))
        // BACKCOMPAT: 2020.1
        SeveritiesProvider.EP_NAME.getPoint(null).registerExtension(testSeverityProvider, testRootDisposable)
    }

    /**
     * Check annotation with info severity
     * Example:
     * ```
     * fun `test function call is highlighted`() {
     *    check_info( """
     *        package  <info descr="Some message">main</info>
     *        a:= "hello"
     *    """.trimIndent())
     * }
     * ```
     *
     * If you want to test silent Annotation (used for highlight some text like rule name of function call), you should
     * use the name of the RegoColor
     *
     * ```
     * fun `test function call is highlighted`() {
     *    check_info( """
     *        package main
     *        <HEAD>aRule</HEAD> {
     *              a = <CALL>fun_obj</CALL>(1).a
     *        }
     *    """.trimIndent())
     * }
     * ```
     */
    fun check_info(@Language("rego") text: String){
        check(text, false, true, false, false)
    }

    /**
     * Check annotation with warn severity
     * Example:
     * ```
     * fun `test warn annotation`() {
     *    check_info( """
     *        package  <warn descr="Some message">main</warn>
     *        a:= "hello"
     *    """.trimIndent())
     * }
     * ```
     */
    fun check_warn(@Language("rego") text: String){
        check(text, true, false, true, false)
    }

    /**
     * check annotation with `error` severity
     *
     * Example:
     * ```
     * fun `test unclosed string is reported as error`() {
     *    check_error( """
     *        package  main
     *        a:= <error descr="Missing closing quote">"</error>
     *    """.trimIndent())
     * }
     * ```
     */
    fun check_error(@Language("rego") text: String){
        check(text, false, false, false, false)
    }

    private fun check(text: String, checkWarnings: Boolean, checkInfos: Boolean, checkWeakWarnings: Boolean, ignoreExtraHighlighting: Boolean){
        myFixture.configureByText("main.rego", text)
        myFixture.checkHighlighting(checkWarnings, checkInfos, checkWeakWarnings, ignoreExtraHighlighting)
    }

}