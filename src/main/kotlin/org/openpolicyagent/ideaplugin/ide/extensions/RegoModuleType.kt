package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import org.openpolicyagent.ideaplugin.lang.RegoIcons
import javax.swing.Icon

class RegoModuleType : ModuleType<RegoModuleBuilder>("Rego Module") {
    override fun createModuleBuilder(): RegoModuleBuilder {
        return RegoModuleBuilder()
    }

    override fun getName(): String {
        return "Rego Project"
    }

    override fun getDescription(): String {
        return "A Rego project is empty. Create a new Rego File to add."
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return RegoIcons.OPA
    }

    override fun createWizardSteps(wizardContext: WizardContext, moduleBuilder: RegoModuleBuilder, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider)
    }



}