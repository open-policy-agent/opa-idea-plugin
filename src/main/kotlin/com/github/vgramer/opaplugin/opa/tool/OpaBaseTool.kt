/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.github.vgramer.opaplugin.opa.tool

import com.intellij.openapi.util.SystemInfo

open class OpaBaseTool {
    val opaBinary =  if (SystemInfo.isWindows) "opa.exe" else "opa"
}