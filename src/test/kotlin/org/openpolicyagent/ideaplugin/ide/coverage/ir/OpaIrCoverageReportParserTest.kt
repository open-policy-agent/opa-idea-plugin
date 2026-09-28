/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package org.openpolicyagent.ideaplugin.ide.coverage.ir

import com.intellij.rt.coverage.data.LineCoverage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.nio.file.Paths

class OpaIrCoverageReportParserTest {

    private val parser = OpaIrCoverageReportParser()
    private val localRoot = Paths.get("/proj/rego")

    @Test
    fun `remapPath strips foreign prefix and moves the path under the local root`() {
        val out = parser.remapPath(
            "/build/agent/rego/policy.rego",
            "rego",
            localRoot,
        )
        assertThat(out).isEqualTo(Paths.get("/proj/rego/policy.rego").toString())
    }

    @Test
    fun `remapPath preserves nested subdirectories beneath the source root`() {
        val out = parser.remapPath(
            "/foreign/rego/sub/dir/helper.rego",
            "rego",
            localRoot,
        )
        assertThat(out).isEqualTo(Paths.get("/proj/rego/sub/dir/helper.rego").toString())
    }

    @Test
    fun `remapPath uses the last segment when source root is multi-segment`() {
        val out = parser.remapPath(
            "/x/policy/rego/p.rego",
            "policy/rego",
            localRoot,
        )
        assertThat(out).isEqualTo(Paths.get("/proj/rego/p.rego").toString())
    }

    @Test
    fun `remapPath returns null when no matching segment exists`() {
        val out = parser.remapPath(
            "/some/other/path/policy.rego",
            "rego",
            localRoot,
        )
        assertThat(out).isNull()
    }

    @Test
    fun `remapPath uses the last occurrence when the source root segment repeats`() {
        val out = parser.remapPath(
            "/Users/rego/git/x/rego/policy.rego",
            "rego",
            localRoot,
        )
        assertThat(out).isEqualTo(Paths.get("/proj/rego/policy.rego").toString())
    }

    @Test
    fun `parse populates ProjectData with FULL line statuses for each covered row`() {
        val json = """
            {
              "files": {
                "/build/agent/rego/policy.rego": {
                  "covered": [
                    {"start":{"row":3},"end":{"row":3}},
                    {"start":{"row":5},"end":{"row":5}},
                    {"start":{"row":11},"end":{"row":11}}
                  ],
                  "not_covered": []
                }
              }
            }
        """.trimIndent()

        val data = parser.parse(json, "rego", localRoot)
        val cd = data.getClassData(Paths.get("/proj/rego/policy.rego").toString())

        assertThat(cd).isNotNull
        assertThat(cd.getLineData(3).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(cd.getLineData(5).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(cd.getLineData(11).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(cd.getLineData(4)).isNull()
    }

    @Test
    fun `parse marks not_covered rows NONE so they count toward the total`() {
        val json = """
            {
              "files": {
                "/x/rego/p.rego": {
                  "covered": [{"start":{"row":3},"end":{"row":3}},{"start":{"row":5},"end":{"row":5}}],
                  "not_covered": [{"start":{"row":7},"end":{"row":7}},{"start":{"row":9},"end":{"row":9}}]
                }
              }
            }
        """.trimIndent()

        val cd = parser.parse(json, "rego", localRoot)
            .getClassData(Paths.get("/proj/rego/p.rego").toString())

        assertThat(cd.getLineData(3).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(cd.getLineData(7).status.toInt()).isEqualTo(LineCoverage.NONE.toInt())
        assertThat(cd.getLineData(9).status.toInt()).isEqualTo(LineCoverage.NONE.toInt())
        // Non-statement rows stay null and are not counted at all.
        assertThat(cd.getLineData(8)).isNull()
    }

    @Test
    fun `parse expands a multi-row not_covered range to every row it spans`() {
        // An unplanned rule's location spans its whole body, not just the head row.
        val json = """{"files":{"/x/rego/p.rego":{"covered":[],"not_covered":[
            {"start":{"row":10},"end":{"row":13}}
        ]}}}"""
        val cd = parser.parse(json, "rego", localRoot)
            .getClassData(Paths.get("/proj/rego/p.rego").toString())

        for (row in 10..13) {
            assertThat(cd.getLineData(row).status.toInt())
                .describedAs("row $row").isEqualTo(LineCoverage.NONE.toInt())
        }
        assertThat(cd.getLineData(9)).isNull()
        assertThat(cd.getLineData(14)).isNull()
    }

    @Test
    fun `parse skips row 0 emitted for synthetic IR statements`() {
        val json = """{"files":{"/x/rego/p.rego":{
            "covered":[{"start":{"row":0},"end":{"row":2}}],
            "not_covered":[{"start":{"row":0},"end":{"row":0}}]
        }}}"""
        val data = parser.parse(json, "rego", localRoot)
        val cd = data.getClassData(Paths.get("/proj/rego/p.rego").toString())
        assertThat(cd.getLineData(0)).isNull()
        assertThat(cd.getLineData(1).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
    }

    @Test
    fun `parse handles multiple files in the same report`() {
        val json = OpaIrCoverageReportParserTest::class.java
            .getResourceAsStream("/org/openpolicyagent/ideaplugin/ide/coverage/sample.json")!!
            .bufferedReader().readText()

        val data = parser.parse(json, "rego", localRoot)

        val policy = data.getClassData(Paths.get("/proj/rego/policy.rego").toString())
        val helpers = data.getClassData(Paths.get("/proj/rego/subpkg/helpers.rego").toString())
        assertThat(policy).isNotNull
        assertThat(helpers).isNotNull
        assertThat(policy.getLineData(13).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        for (row in 19..21) {
            assertThat(policy.getLineData(row).status.toInt())
                .describedAs("row $row").isEqualTo(LineCoverage.NONE.toInt())
        }
        assertThat(helpers.getLineData(6).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
    }

    @Test
    fun `parse gives the original-case and lowercased keys independent LineData instances`() {
        // The platform mutates hit counts in place, so the two keys must not alias one array.
        val json = """{"files":{"/x/rego/Policy.rego":{
            "covered":[{"start":{"row":3},"end":{"row":3}}],
            "not_covered":[{"start":{"row":5},"end":{"row":5}}]
        }}}"""
        val data = parser.parse(json, "rego", localRoot)
        val original = Paths.get("/proj/rego/Policy.rego").toString()
        val lowercased = original.lowercase()
        assertThat(lowercased).isNotEqualTo(original)

        val cd = data.getClassData(original)
        val lower = data.getClassData(lowercased)
        assertThat(cd).isNotNull
        assertThat(lower).isNotNull
        assertThat(cd.getLineData(3)).isNotSameAs(lower.getLineData(3))

        cd.getLineData(3).setStatus(LineCoverage.NONE)
        assertThat(lower.getLineData(3).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
    }

    @Test
    fun `parseAll merges covered from one report over not_covered in another`() {
        val a = """{"files":{"/x/rego/p.rego":{
            "covered":[{"start":{"row":4},"end":{"row":4}}],
            "not_covered":[{"start":{"row":6},"end":{"row":6}}]
        }}}"""
        val b = """{"files":{"/x/rego/p.rego":{
            "covered":[],
            "not_covered":[{"start":{"row":4},"end":{"row":4}},{"start":{"row":6},"end":{"row":6}}]
        }}}"""

        for (reports in listOf(listOf(a, b), listOf(b, a))) {
            val cd = parser.parseAll(reports, "rego", localRoot)
                .getClassData(Paths.get("/proj/rego/p.rego").toString())
            assertThat(cd.getLineData(4).status.toInt())
                .describedAs("row 4 covered in one report wins")
                .isEqualTo(LineCoverage.FULL.toInt())
            assertThat(cd.getLineData(6).status.toInt())
                .describedAs("row 6 uncovered everywhere")
                .isEqualTo(LineCoverage.NONE.toInt())
        }
    }

    @Test
    fun `parseAll keeps files that appear in only one report`() {
        val a = """{"files":{"/x/rego/only_a.rego":{
            "covered":[{"start":{"row":2},"end":{"row":2}}],"not_covered":[]
        }}}"""
        val b = """{"files":{"/x/rego/only_b.rego":{
            "covered":[],"not_covered":[{"start":{"row":9},"end":{"row":9}}]
        }}}"""

        val data = parser.parseAll(listOf(a, b), "rego", localRoot)
        val onlyA = data.getClassData(Paths.get("/proj/rego/only_a.rego").toString())
        val onlyB = data.getClassData(Paths.get("/proj/rego/only_b.rego").toString())
        assertThat(onlyA.getLineData(2).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(onlyB.getLineData(9).status.toInt()).isEqualTo(LineCoverage.NONE.toInt())
    }

    @Test
    fun `parseAll unions rows so the array is sized for the highest row seen`() {
        val a = """{"files":{"/x/rego/p.rego":{
            "covered":[{"start":{"row":2},"end":{"row":2}}],"not_covered":[]
        }}}"""
        val b = """{"files":{"/x/rego/p.rego":{
            "covered":[{"start":{"row":40},"end":{"row":41}}],"not_covered":[]
        }}}"""

        val cd = parser.parseAll(listOf(a, b), "rego", localRoot)
            .getClassData(Paths.get("/proj/rego/p.rego").toString())
        assertThat(cd.getLineData(2).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(cd.getLineData(40).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
        assertThat(cd.getLineData(41).status.toInt()).isEqualTo(LineCoverage.FULL.toInt())
    }

    @Test
    fun `parseAll returns empty ProjectData for an empty report list`() {
        val data = parser.parseAll(emptyList(), "rego", localRoot)
        assertThat(data.getClassData(Paths.get("/proj/rego/p.rego").toString())).isNull()
    }

    @Test
    fun `parse returns empty ProjectData when files key is absent`() {
        val data = parser.parse("""{"other":{}}""", "rego", localRoot)
        assertThat(data.getClassData(Paths.get("/proj/rego/anything.rego").toString())).isNull()
    }
}
