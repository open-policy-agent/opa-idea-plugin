/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

// IMPORTANT NOTE: this file is a  portage of time/format.go in kotlin

// Original Copyright:
//
// Copyright 2010 The Go Authors. All rights reserved.
// license that can be found in the docs/devel/golang/golang-license.md file.
package org.openpolicyagent.ideaplugin.ide.runconfig.test.go

private const val Nanosecond: Long = 1
private const val Microsecond: Long = 1000 * Nanosecond
private const val Millisecond: Long = 1000 * Microsecond
private const val Second: Long = 1000 * Millisecond
private const val Minute: Long = 60 * Second
private const val Hour: Long = 60 * Minute


private val unitMap = mapOf(
    "ns" to Nanosecond,
    "us" to Microsecond,
    "µs" to Microsecond, // U+00B5 = micro symbol
    "μs" to Microsecond, // U+03BC = Greek letter mu
    "ms" to Millisecond,
    "s" to Second,
    "m" to Minute,
    "h" to Hour
)


/**
 * A Duration represents the elapsed time between two instants
 * as an [Long] nanosecond count. The representation limits the
 * largest representable duration to approximately 290 years.
 */
class Duration(private val value: Long) {
    fun toMilliseconds(): Long {
        return value / 1000000 // 1e6
    }
}

/**
 * Throw when the conversion of a string to a Duration failed because the input string was not valid
 */
class DurationFormatException(msg: String) : NumberFormatException(msg)


/**
 * ParseDuration parses a duration string.
 * A duration string is a possibly signed sequence of
 * decimal numbers, each with optional fraction and a unit suffix,
 * such as "300ms", "-1.5h" or "2h45m".
 * Valid time units are "ns", "us" (or "µs"), "ms", "s", "m", "h".
 *
 * Throws [DurationFormatException] if the s is not a valid duration
 */
fun parseDuration(s1: String): Duration {
    var s = s1
    // [-+]?([0-9]*(\.[0-9]*)?[a-z]+)+
    val orig = s
    var d: Long = 0
    var neg = false

    // Consume [-+]?
    if (s != "") {
        val c = s[0]
        if (c == '-' || c == '+') {
            neg = c == '-'
            s = s.substring(1)
        }
    }
    // Special case: if all that is left is "0", this is zero.
    if (s == "0") {
        return Duration(0)
    }
    if (s == "") {
        throw DurationFormatException("invalid duration ${orig}")
    }
    while (s != "") {

        var f: Long = 0     // integers before, after decimal point
        var v: Long
        var scale: Double = 1.0 // value = v + f/scale

        // The next character must be [0-9.]
        if (!(s[0] == '.' || s[0] in '0'..'9')) {
            throw DurationFormatException("invalid duration ${orig}")
        }
        // Consume [0-9]*
        var pl = s.length
        val (vt, st) = leadingInt(s)
        v = vt
        s = st

        val pre = pl != s.length // whether we consumed anything before a period

        // Consume (\.[0-9]*)?
        var post = false
        if (s != "" && s[0] == '.') {
            s = s.substring(1)
            pl = s.length
            val (ft, scaleTemp, stt) = leadingFraction(s)
            f = ft
            scale = scaleTemp.toDouble()
            s = stt
            post = pl != s.length
        }
        if (!pre && !post) {
            // no digits (e.g. ".s" or "-.s")
            throw DurationFormatException("invalid duration $orig")
        }

        // Consume unit.

        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '.' || c in '0'..'9') {
                break
            }
            i++
        }
        if (i == 0) {
            throw DurationFormatException("missing unit in duration $orig")
        }
        val u = s.substring(0, i)
        s = s.substring(i)


        val unit = unitMap[u] ?: throw DurationFormatException("unknown unit $u in duration $orig")
        if (v > Long.MAX_VALUE / unit) {
            // overflow
            throw DurationFormatException("invalid duration $orig")
        }
        v *= unit
        if (f > 0) {
            // float64 is needed to be nanosecond accurate for fractions of hours.
            // v >= 0 && (f*unit/scale) <= 3.6e+12 (ns/h, h is the largest unit)
            v += (f.toDouble() * (unit.toDouble() / scale)).toLong()
            if (v < 0) {
                // overflow
                throw DurationFormatException("invalid duration $orig")
            }
        }
        d += v
        if (d < 0) {
            // overflow
            throw DurationFormatException("invalid duration $orig")
        }
    }

    if (neg) {
        d = -d
    }
    return Duration(d)
}

/**
 * leadingInt consumes the leading [0-9]* from s.
 */
private fun leadingInt(s: String): Pair<Long, String> {
    var i = 0
    var x: Long = 0
    while (i < s.length) {
        val c = s[i]
        if (c < '0' || c > '9') {
            break
        }
        if (x > Long.MAX_VALUE / 10) {
            // overflow
            throw DurationFormatException("bad [0-9]*")
        }
        x = x * 10 + c.code.toLong() - '0'.code.toLong()
        if (x < 0) {
            // overflow
            throw DurationFormatException("bad [0-9]*")
        }
        i++
    }
    return Pair(x, s.substring(i))
}

/**
 * leadingFraction consumes the leading [0-9]* from s.
 * It is used only for fractions, so does not return an error on overflow,
 * it just stops accumulating precision.
 */
private fun leadingFraction(s: String): Triple<Long, Long, String> {
    var i = 0
    var x: Long = 0
    var scale: Long = 1
    var overflow = false
    while (i < s.length) {
        val c = s[i]
        if (c < '0' || c > '9') {
            break
        }
        if (overflow) {
            continue
        }
        if (x > Long.MAX_VALUE / 10) {
            // It's possible for overflow to give a positive number, so take care.
            overflow = true
            continue
        }
        val y = x * 10 + c.code.toLong() - '0'.code.toLong()
        if (y < 0) {
            overflow = true
            continue
        }
        x = y
        scale *= 10
        i++
    }
    return Triple(x, scale, s.substring(i))
}
