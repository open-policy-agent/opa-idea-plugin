package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ui.wizard.WizardStep
import javax.swing.JComponent
import javax.swing.JLabel

class RegoModuleWizardStep : ModuleWizardStep() {
    override fun updateDataModel() {
        //TODO: what does this do
    }

    override fun getComponent(): JComponent {
        return JLabel("A Rego project is empty. Create a new Rego File to add.")
    }
}