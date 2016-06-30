package org.rust.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.rust.ide.colors.RustColor
import org.rust.ide.highlight.syntax.RustHighlighter
import org.rust.lang.core.psi.*
import org.rust.lang.core.psi.impl.mixin.isMut
import org.rust.lang.core.psi.util.elementType

// Highlighting logic here should be kept in sync with tags in RustColorSettingsPage
class RustHighlightingAnnotator : Annotator {

    override fun annotate(element: PsiElement, _holder: AnnotationHolder) {
        val highlighter = wrap(_holder)
        if (element is RustReferenceElement) {
            // Highlight the element dependent on what it's referencing.
            val ref = element.reference.resolve() ?: return

            val text = when (element) {
                is RustPathElement -> element.identifier
                else -> element
            }
            resolveColor(ref)?.let { highlighter.highlight(text, it) }
        } else {
            element.accept(highlightingVisitor(highlighter))
        }
    }

    // Capture the color of the element w/o mutating anything.
    fun resolveColor(e: PsiElement): RustColor? {
        var outColor: RustColor? = null;
        val resolver = highlightingVisitor(object : Highlighter { // @TODO Could use RustComputingVisitor
            override fun highlight(element: PsiElement?, color: RustColor?) {
                assert(outColor == null, { "$e is not a leaf element, ambiguous color." })
                outColor = color;
            }
        })

        e.accept(resolver)
        return outColor
    }

    fun highlightingVisitor(holder: Highlighter) = object : RustElementVisitor() {

        fun highlight(element: PsiElement?, color: RustColor?) = holder.highlight(element, color)

        override fun visitLitExpr(o: RustLitExprElement) {
            // Re-highlight literals in attributes
            if (o.parent is RustMetaItemElement) {
                val literal = o.firstChild
                holder.highlight(literal, RustHighlighter.map(literal.elementType))
            }
        }

        override fun visitTypeParam(o: RustTypeParamElement) {
            highlight(o.identifier, RustColor.TYPE_PARAMETER)
        }

        override fun visitAttr(o: RustAttrElement) = highlight(o, RustColor.ATTRIBUTE)

        override fun visitTraitRef(o: RustTraitRefElement) = highlight(o.path.identifier, RustColor.TRAIT)

        override fun visitPatBinding(o: RustPatBindingElement) {
            if (o.isMut) {
                highlight(o.identifier, RustColor.MUT_BINDING)
            }
        }

        override fun visitEnumItem(o: RustEnumItemElement)       = highlight(o.identifier, RustColor.ENUM)
        override fun visitStructItem(o: RustStructItemElement)   = highlight(o.identifier, RustColor.STRUCT)
        override fun visitTraitItem(o: RustTraitItemElement)     = highlight(o.identifier, RustColor.TRAIT)
        override fun visitModDeclItem(o: RustModDeclItemElement) = highlight(o.identifier, RustColor.MODULE)
        override fun visitModItem(o: RustModItemElement)         = highlight(o.identifier, RustColor.MODULE)

        //@TODO When are element and element.identifier different?
        override fun visitMacroInvocation(m: RustMacroInvocationElement) = highlight(m, RustColor.MACRO)
        override fun visitMethodCallExpr(o: RustMethodCallExprElement)   = highlight(o.identifier, RustColor.INSTANCE_METHOD)
        override fun visitFnItem(o: RustFnItemElement)                   = highlight(o.identifier, RustColor.FUNCTION_DECLARATION)

        override fun visitImplMethodMember(o: RustImplMethodMemberElement) =
            highlight(o.identifier, if (o.isStatic) RustColor.STATIC_METHOD else RustColor.INSTANCE_METHOD)
        override fun visitTraitMethodMember(o: RustTraitMethodMemberElement) =
            highlight(o.identifier, if (o.isStatic) RustColor.STATIC_METHOD else RustColor.INSTANCE_METHOD)

    }
}

interface Highlighter {
    fun highlight(element: PsiElement?, color: RustColor?);
}

fun wrap(holder: AnnotationHolder): Highlighter {
    return object: Highlighter{

        override fun highlight(element: PsiElement?, color: RustColor?) {
            val textAttributesKey = color?.textAttributesKey
            if (element != null && textAttributesKey != null) {
                holder.createInfoAnnotation(element, null).textAttributes = textAttributesKey
            }
        }

    }
}
