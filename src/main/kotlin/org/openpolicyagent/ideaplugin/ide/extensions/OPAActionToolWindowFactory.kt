package org.openpolicyagent.ideaplugin.ide.extensions


import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory


class OPAActionToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        //no need for default tab
        toolWindow.hide(null)
    }

}