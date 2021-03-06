package org.rust.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.rust.ide.intentions.RemoveParenthesesFromExprIntention
import org.rust.lang.core.psi.*

class RustExpressionAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is RustIfExprElement    -> checkIfParentheses(element, holder)
            is RustWhileExprElement -> checkWhileParentheses(element, holder)
            is RustRetExprElement   -> checkReturnParentheses(element, holder)
            is RustMatchExprElement -> checkMatchParentheses(element, holder)
            is RustForExprElement   -> checkForParentheses(element, holder)
            is RustParenExprElement -> checkImmediateChildIsParen(element, holder)
        }
    }

    private fun checkImmediateChildIsParen(element: RustParenExprElement, holder: AnnotationHolder) {
        val childExpr = element.expr
        if (childExpr is RustParenExprElement) {
            holder.createWeakWarningAnnotation(element, "Redundant parentheses in expression")
                .registerFix(RemoveParenthesesFromExprIntention())
        }
    }

    private fun checkIfParentheses(element: RustIfExprElement, holder: AnnotationHolder) {
        val expr = element.expr
        if (expr is RustParenExprElement) {
            holder.createWeakWarningAnnotation(expr, "Predicate expression has unnecessary parentheses")
                .registerFix(RemoveParenthesesFromExprIntention())
        }
    }

    private fun checkWhileParentheses(element: RustWhileExprElement, holder: AnnotationHolder) {
        val expr = element.expr
        if (expr is RustParenExprElement) {
            holder.createWeakWarningAnnotation(expr, "Predicate expression has unnecessary parentheses")
                .registerFix(RemoveParenthesesFromExprIntention())
        }
    }

    private fun checkMatchParentheses(element: RustMatchExprElement, holder: AnnotationHolder) {
        val expr = element.expr
        if (expr is RustParenExprElement) {
            holder.createWeakWarningAnnotation(expr, "Match expression has unnecessary parentheses")
                .registerFix(RemoveParenthesesFromExprIntention())
        }
    }

    private fun checkReturnParentheses(element: RustRetExprElement, holder: AnnotationHolder) {
        val expr = element.expr
        if (expr is RustParenExprElement) {
            holder.createWeakWarningAnnotation(expr, "Return expression has unnecessary parentheses")
                .registerFix(RemoveParenthesesFromExprIntention())
        }
    }

    private fun checkForParentheses(element: RustForExprElement, holder: AnnotationHolder) {
        val expr = element.scopedForDecl.expr
        if (expr is RustParenExprElement) {
            holder.createWeakWarningAnnotation(expr, "For loop expression has unnecessary parentheses")
                .registerFix(RemoveParenthesesFromExprIntention())
        }
    }

    private val RustIfExprElement.expr: RustExprElement?
        get() = exprList.firstOrNull()
}
