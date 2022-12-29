/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.openpolicyagent.ideaplugin.opa.project.settings

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.gridLayout.HorizontalAlign

import com.intellij.ui.dsl.builder.panel

/**
 * UI for the opa setting options.
 */
class OpaOptionsConfigurable(private val project: Project) :
    BoundSearchableConfigurable("Opa", "Opa Options", "Settings.Project.Opa") {

    private val settings
        get() = OpaProjectSettings.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row("OPA check options:") {
               textField()
                    .bindText(settings::opaCheckOptions)
                   .horizontalAlign(HorizontalAlign.FILL)
            }
        }
    }
}
