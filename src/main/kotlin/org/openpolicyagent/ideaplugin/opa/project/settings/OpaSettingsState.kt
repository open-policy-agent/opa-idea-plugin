/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.opa.project.settings

import com.intellij.openapi.components.BaseState

class OpaSettingsState:  BaseState() {
    var opaCheckOptions = OpaProjectSettings.defaultOpaCheckOptions
    var regalPath = ""
    var regalVerboseLogging = false // Applies to both Language Server and DAP server
}
