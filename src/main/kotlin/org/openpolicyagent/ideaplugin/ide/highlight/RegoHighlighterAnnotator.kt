package org.openpolicyagent.ideaplugin.ide.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import org.openpolicyagent.ideaplugin.ide.colors.RegoColor
import org.openpolicyagent.ideaplugin.lang.psi.*

class RegoHighlighterAnnotator : Annotator {

    private fun styleForDeclType(definition: Any) = when (definition) {
        is RegoRule -> RegoColor.HEAD
        is RegoExprCall -> RegoColor.CALL
        is RegoEmptySet -> RegoColor.CALL
        else -> null
    }

    //todo: better way to record colors used in annotator?
    val usedColors = listOf(RegoColor.HEAD.textAttributesKey, RegoColor.CALL.textAttributesKey)

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is RegoRule){
            val style = styleForDeclType(element)
            if (style != null){
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.ruleHead.`var`.textRange)
                    .textAttributes( style.textAttributesKey)
                    .create()
            }
        }

        if (element is RegoExprCall){
            val style = styleForDeclType(element)
            if (style != null){
                val varlist = element.refArgDotList
                val textRange = if(varlist.size >= 1) varlist[varlist.size - 1].`var`.textRange else element.`var`.textRange

                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(textRange)
                    .textAttributes( style.textAttributesKey)
                    .create()
            }

        }

        if (element is RegoEmptySet){
            val style = styleForDeclType(element)
            if (style != null){
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes( style.textAttributesKey)
                    .create()
            }
        }

    }
}