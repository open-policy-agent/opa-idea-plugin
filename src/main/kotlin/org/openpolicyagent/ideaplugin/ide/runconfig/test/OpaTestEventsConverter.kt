/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.ide.runconfig.test

import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.ServiceMessageBuilder
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.openapi.util.Key
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor
import org.openpolicyagent.ideaplugin.ide.runconfig.test.go.parseDuration


/**
 * This class process the output of the `opa test` command to generate the test tree. The core logic is implemented
 * in [processServiceMessages]
 *
 * `Opa test` command must be run with the parameters "-v -f pretty" and optionally "--explain ", otherwise the output
 * can not be parsed by this class
 */
class OpaTestEventsConverter(
    testFrameworkName: String,
    consoleProperties: TestConsoleProperties
) : OutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties) {

    companion object {
        private val passTestRegex = Regex("([a-zA-Z0-9_\\.]+): PASS \\((.*)\\)")
        private val failTestRegex = Regex("([a-zA-Z0-9_\\.]+): (FAIL|ERROR) \\((.*)\\)")
        private val lastMsgPrefix = Regex("Process finished with exit code.*")
        private const val ROOT_NODE_ID = "0"
        private const val SUMMARY = "SUMMARY"
    }


    private var state: State = State.START

    private enum class State {
        /**
         * We are ready to process a new test
         */
        START,
        /**
         * We are currently aggregating query explanation of the [currentFailTest]
         */
        FAIL_OUTPUT
    }

    /**
     * memorize all Fail /Error test to avoid to fire them twice
     *
     * see [createCurrentFailedTest] for more info
     */
    private val failTestIds = mutableSetOf<String>()

    /**
     * hold information about the current Fail /Error test.
     * see [FailTest] for more info
     */
    private var currentFailTest: FailTest? = null


    /**
     * Hold information about the Fail / Error test
     *
     * [id]: the test name. Also use as node Id in the test tree
     * [duration]: test duration in milliseconds as a String
     * [output]: the test header + the query explanation (will be updated until we encounter an endMsg (see [isEndMsg])
     *
     * Example:
     *
     * data.test.main.test_should_fail: FAIL (298.521µs)
     *  query:1               Enter data.test.main.test_should_fail = _
     *  test_main.rego:15     | Enter data.test.main.test_should_fail
     *  test_main.rego:16     | | Fail 2 = 1
     *  query:1               | Fail data.test.main.test_should_fail = _
     *
     *
     * [id] = "data.test.main.test_should_fail"
     * [duration] = "0" because 298.521µs = 0,298521ms
     * [output] = all the lines
     *
     *
     * see [createCurrentFailedTest] for more information
     */
    private data class FailTest(val id: String, val duration: String, var output: String)

    /**
     * This method is call on each output line by the IntelliJ test framework runner. Lines are parsed in order to fire
     * events to generate the test tree.
     *
     * In order to be able to show the query explanation in the node output. we use the `-v -f pretty` output format of
     * `opa test` command. The output of the command executed by IntelliJ may look this( without the line numbers):
     *
     * 1)  Testing started at .+
     * 2)  opa test \-f pretty \-v \-b .+
     * 3)  FAILURES
     * 4)  --------------------------------------------------------------------------------
     * 5)  data.test_rules.one.ok.one.ko.test_rule1_should_be_ko: FAIL (1.015625ms)
     * 6)
     * 7)    query:1                       Enter data.test_rules.one.ok.one.ko.test_rule1_should_be_ko = _
     * 8)    test_rules_1_ok_ko.rego:6     | Enter data.test_rules.one.ok.one.ko.test_rule1_should_be_ko
     * 9)    main.rego:3                   | | Enter data.main.rule1
     * 10)   main.rego:6                   | | | Note "rule 1 trace"
     * 11)   test_rules_1_ok_ko.rego:8     | | Fail msg = {"msg from rule 1"}
     * 12)   query:1                       | Fail data.test_rules.one.ok.one.ko.test_rule1_should_be_ko = _
     * 13)
     * 14) data.test_rules.one.ok.one.ko.test_rule_2_should_be_ko: FAIL (493.993µs)
     * 15)
     * 16)   query:1                        Enter data.test_rules.one.ok.one.ko.test_rule_2_should_be_ko = _
     * 17)   test_rules_1_ok_ko.rego:12     | Enter data.test_rules.one.ok.one.ko.test_rule_2_should_be_ko
     * 18)   test_rules_1_ok_ko.rego:14     | | Fail msg = {"another message in order to put the test in FAIL state"}
     * 19)   query:1                        | Fail data.test_rules.one.ok.one.ko.test_rule_2_should_be_ko = _
     * 20)
     * 21) SUMMARY
     * 22) --------------------------------------------------------------------------------
     * 23) data.test_rules.one.ok.one.ko.test_rule1_should_be_ko: FAIL (1.015625ms)
     * 24) data.test_rules.one.ok.one.ko.test_rule_2_should_be_ko: FAIL (493.993µs)
     * 25) data.test_rules.one.ok.one.ko.test_should_be_ok: PASS (419.351µs)
     * 26) data.test_rules.one.ok.one.ko.test_should_raise_error: ERROR (570.322µs)
     * 27)   test_rules_1_ok_ko.rego:25: eval_builtin_error: to_number: strconv.ParseFloat: parsing "x": invalid syntax
     * 28) --------------------------------------------------------------------------------
     * 29) PASS: 1/4
     * 30) FAIL: 2/4
     * 31) ERROR: 1/4
     * 32)
     * 33) Process finished with exit code 2
     *
     * Lines 3 to 32 correspond to the output of the opa command, the other lines are IntelliJ debugging information.
     *
     * The Fail or Error information are spread across lines because this function is called for each line, we need to
     * track to keep track of state and memorize information (ie query explanation) about Fail/ Error test. To achieve
     * this goal we use 2 variables
     *  [state]: indicate if we a ready to precess a new test, or add the line to output of the [currentFailTest]
     *  [currentFailTest]: hold information about the test (id, test duration , output)
     *
     *  First [state] is initialize to [State.START] meaning we are ready to process a new test.
     *
     *  If we encounter a Fail/ Error Test (eg L3, L12, L24) Then state switch to [State.FAIL_OUTPUT] and [currentFailTest]
     *  is initialized with this test.
     *  The next lines are appended to the output of [currentFailTest] until we encounter a line which is not the part of
     *  the query explanation. It can be another test or the begging of a new section (eg L12, L19, L20, L23, L26).
     *  Finally a failedTest event is triggered with the information of [currentFailTest] to add the fail test
     *  in the test tree and [state] is switched to [State.START]
     *
     *  If we encounter a Pass test, we directly trigger an event to add the pass test in the tree. Pass test information
     *  is contained on one line (eg L23)
     *
     *  Note:
     *      1) because fail tests hearer are present twice in the output (eg L3 and L21, L12 and L22), we keep track of
     *  the fail / error test that has been already processed in [failTestIds] set.
     *
     *      2) To make debugging easier, the root node output contains the full output of the command (L1 to L33). IntelliJ
     *  automatically add the output of the other nodes to the root. So we have to add the lines which are part of a test
     *  result (eg L1-4, L21-22, and L29-33).
     */
    override fun processServiceMessages(text: String, outputType: Key<*>, visitor: ServiceMessageVisitor): Boolean {
        when (state) {
            State.START -> {
                if (isPassTest(text)) {
                    firePassedTest(text, outputType, visitor)
                }
                else if (isFailTest(text)) {
                    // to avoid duplicate fail if we are in the summary section. Error test are only in Summary section
                    if (createCurrentFailedTest(text)) {
                        state = State.FAIL_OUTPUT
                    }
                }
                else {
                    // Add the `text` that is not part of the test result to the root node. (eg L1-4,L21-22 L29-33).
                    processor.onUncapturedOutput(text, outputType)
                }
            }
            State.FAIL_OUTPUT -> {
                when {
                    isEndMsg(text) -> { // handle the case the the last test fail (eg L21) or an error test(eg L28)
                        fireFailedTest(currentFailTest!!, outputType, visitor)
                        state = State.START
                    }

                    isPassTest(text) -> {
                        fireFailedTest(currentFailTest!!, outputType, visitor)
                        firePassedTest(text, outputType, visitor)
                        state = State.START
                    }

                    isFailTest(text) -> { // eg L14
                        fireFailedTest(currentFailTest!!, outputType, visitor)
                        createCurrentFailedTest(text)
                        state = State.FAIL_OUTPUT
                    }

                    else -> { // this line is part of the query explanation so we append it the current currentFailTest
                        currentFailTest!!.output += text
                    }
                }
            }
        }

        return true
    }


    /**
     * Return true if the text is information about a Pass test
     *
     * example: data.test.main.test_rule1_should_be_ok: PASS (465.471µs)
     */
    private fun isPassTest(text: String): Boolean {
        return passTestRegex.matches(text.trim())
    }

    /**
     * Return true if the text is information or header of a Fail or Error Test
     *
     * example of fail test: data.test.main.test_rule_2_should_be_ko: FAIL (384.653µs)
     * example of error test: data.test.main.test_should_raise_error: ERROR (718.549µs)
     */
    private fun isFailTest(text: String): Boolean {
        return failTestRegex.matches(text.trim())
    }

    /**
     * Return true if the text is the end of Fail / Error test.
     *
     * That's means previous line was last line of the query explanation
     */
    private fun isEndMsg(text: String): Boolean {
        val trimText = text.trim()

        return trimText.equals(SUMMARY, true) // Beginning of the Summary section
                || trimText.matches(lastMsgPrefix) // last line print in the output, it's not print by opa but by process Handler
                || trimText.matches("-+".toRegex())
    }

    /**
     * initialize [currentFailTest] with text only this test has not been recorded yet.
     *
     * Failed test appear twice in the output of `opa test`. Once with the query explanation and once in he Summary
     * section.
     *
     * Returns true if the currentFailTest has been initialize, false otherwise
     */
    private fun createCurrentFailedTest(text: String): Boolean {
        val (testId, _, durationString) = failTestRegex.find(text.trim())!!.destructured
        val duration = parseDuration(durationString).toMilliseconds().toString()

        if (!failTestIds.contains(testId)) {
            currentFailTest = FailTest(testId, duration, text.trim())
            failTestIds.add(testId)
            return true
        }
        return false
    }

    /**
     * Send event to add a Fail / Error node to the test tree
     */
    private fun fireFailedTest(failTest: FailTest, outputType: Key<*>, visitor: ServiceMessageVisitor) {
        // TODO add addAttribute locationHint to link node to the source file (ie when the user double click on the
        //  node it open the source file at the line of the test.

        val ts = ServiceMessageBuilder.testStarted(failTest.id)
            .addAttribute("nodeId", failTest.id)
            .addAttribute("parentNodeId", ROOT_NODE_ID)
        super.processServiceMessages(ts.toString(), outputType, visitor)

        val te = ServiceMessageBuilder.testFailed(failTest.id)
            .addAttribute("nodeId", failTest.id)
            .addAttribute("parentNodeId", ROOT_NODE_ID)
            .addAttribute("duration", failTest.duration)
            .addAttribute("message", failTest.output)
        super.processServiceMessages(te.toString(), outputType, visitor)
    }

    /**
     * Send event to add a Pass node to the test tree
     */
    private fun firePassedTest(text: String, outputType: Key<*>, visitor: ServiceMessageVisitor) {
        val (testId, durationString) = passTestRegex.find(text.trim())!!.destructured
        val duration = parseDuration(durationString).toMilliseconds().toString()

        // TODO add addAttribute locationHint to link node to the source file (ie when the user double click on the
        //  node it open the source file at the line of the test.

        val ts = ServiceMessageBuilder.testStarted(testId)
            .addAttribute("nodeId", testId)
            .addAttribute("parentNodeId", ROOT_NODE_ID)
        super.processServiceMessages(ts.toString(), outputType, visitor)

        val te = ServiceMessageBuilder.testFinished(testId)
            .addAttribute("nodeId", testId)
            .addAttribute("parentNodeId", ROOT_NODE_ID)
            .addAttribute("duration", duration)
        super.processServiceMessages(te.toString(), outputType, visitor)
    }
}
