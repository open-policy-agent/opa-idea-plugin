package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.writeChild
import org.openpolicyagent.ideaplugin.lang.RegoIcons


class RegoModuleBuilder : ModuleBuilder() {
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        super.setupRootModel(modifiableRootModel)
    }

    override fun setupModule(module: Module?) {
        super.setupModule(module)
    }

    override fun getModuleType(): ModuleType<*> {
        return RegoModuleType()
    }

    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return arrayOf(RegoModuleWizardStep())
    }


}