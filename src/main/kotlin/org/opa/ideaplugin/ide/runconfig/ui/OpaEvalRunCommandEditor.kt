/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.opa.ideaplugin.ide.runconfig.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileTypeDescriptor
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.intellij.util.text.nullize
import org.opa.ideaplugin.ide.runconfig.OpaEvalRunConfiguration
import java.awt.Dimension
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Ui to create / edit a [org.opa.ideaplugin.ide.runconfig.OpaEvalRunConfiguration]
 */
class OpaEvalRunCommandEditor(private val project: Project) : SettingsEditor<OpaEvalRunConfiguration>() {

    private var query = JBTextField()
    private var input = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            TextBrowseFolderListener(
                FileTypeDescriptor("opa input file", ".json")
            )
        )

    }
    private var bundle = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            TextBrowseFolderListener(
                FileChooserDescriptor(false, true, false, false, false, false)
                    .withRoots(*ProjectRootManager.getInstance(project).contentRootsFromAllModules)
            )
        )
    }
    private var additionalArgs = RawCommandLineEditor()

    override fun createEditor() = panel {
        row("Query:") {
            query()
        }

        row("Input:") {
            input()
        }

        row("Bundle:") {
            bundle()
        }

        row("Additional Args:") {
            additionalArgs.apply { preferredSize = Dimension(1000, height) }()
        }
    }

    /**
     * save the parameters to the configuration (ie call when user click to 'apply' to save the run configuration)
     */
    override fun applyEditorTo(s: OpaEvalRunConfiguration) {
        s.query = query.text
        s.input = input.text.toPath()
        s.bundleDir = bundle.text.toPath()
        s.additionalArgs = additionalArgs.text
    }

    /**
     * restore to configuration to the ui components (call when the configuration is loaded in ui)
     */
    override fun resetEditorFrom(s: OpaEvalRunConfiguration) {
        query.text = s.query ?: ""
        input.text = s.input?.toString() ?: ""
        bundle.text = s.bundleDir?.toString() ?: ""
        additionalArgs.text = s.additionalArgs ?: ""
    }

    private fun String.toPath(): Path? {
        return nullize()?.let { Paths.get(it) }
    }
}