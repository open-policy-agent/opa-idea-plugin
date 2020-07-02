package org.openpolicyagent.ideaplugin.ide.extensions

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.openpolicyagent.ideaplugin.ide.colors.RegoColor
import org.openpolicyagent.ideaplugin.lang.psi.*

class RegoHighlightorAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is RegoRule){
            val style = RegoColor.HEAD
            val annotation = holder.createInfoAnnotation(element.ruleHead.`var`.textRange, null)
            annotation.textAttributes = style.textAttributesKey
        }
        if (element is RegoExprCall){
            val style = RegoColor.CALL
            val varlist = element.varList
            if (varlist.size > 1){
                val annotation = holder.createInfoAnnotation(varlist[1].textRange, null)
                annotation.textAttributes = style.textAttributesKey
            } else {
                val annotation = holder.createInfoAnnotation(varlist[0], null)
                annotation.textAttributes = style.textAttributesKey
            }
        }
        if (element is RegoEmptySet){
            val style = RegoColor.CALL
            val annotation = holder.createInfoAnnotation(element, null)
            annotation.textAttributes = style.textAttributesKey
        }

//    if (element is RegoLiteral){
//        val style = RegoColor.VAR
//        val elems = element.
//        if (term is RegoVar){
//            val annotation = holder.createInfoAnnotation(element.ter, null)
//            annotation.textAttributes = style.textAttributesKey
//        }
//
//    }
    }
}