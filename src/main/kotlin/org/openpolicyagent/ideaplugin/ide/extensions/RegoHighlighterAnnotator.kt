package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
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
            val annotation = holder.createInfoAnnotation(element.ruleHead.`var`.textRange, null)
            if (style != null){
                annotation.textAttributes = style.textAttributesKey
            }
        }

        if (element is RegoExprCall){
            val style = styleForDeclType(element)
            val varlist = element.refArgDotList
            if (varlist.size >= 1) {
                val annotation = holder.createInfoAnnotation(varlist[varlist.size - 1].`var`.textRange, null)
                if (style != null){
                    annotation.textAttributes = style.textAttributesKey
                }
            } else {
                val annotation = holder.createInfoAnnotation(element.`var`.textRange, null)
                if (style != null){
                    annotation.textAttributes = style.textAttributesKey
                }
            }
        }

        if (element is RegoEmptySet){
            val style = styleForDeclType(element)
            val annotation = holder.createInfoAnnotation(element, null)
            if (style != null){
                annotation.textAttributes = style.textAttributesKey
            }
        }

    }
}