/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.opa.project.settings

import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@State(name = "OPASettings", storages = [(Storage("opa.xml"))])
class OpaProjectSettings(val project: Project) : SimplePersistentStateComponent<OpaSettingsState>(OpaSettingsState()) {

    var opaCheckOptions
        get() = state.opaCheckOptions
        set(value) {
            state.opaCheckOptions = value
        }

    companion object {
        @JvmStatic
        val defaultOpaCheckOptions = "--strict"

        @JvmStatic
        fun getInstance(project: Project): OpaProjectSettings {
            return project.service()
        }
    }
}