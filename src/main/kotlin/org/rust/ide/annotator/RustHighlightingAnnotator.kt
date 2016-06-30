package org.rust.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import org.rust.ide.colors.RustColor
import org.rust.ide.highlight.syntax.RustHighlighter
import org.rust.lang.core.psi.*
import org.rust.lang.core.psi.impl.mixin.isMut
import org.rust.lang.core.psi.util.elementType

// Highlighting logic here should be kept in sync with tags in RustColorSettingsPage
class RustHighlightingAnnotator : Annotator {

    override fun annotate(element: PsiElement, _holder: AnnotationHolder) {
        element.accept(object : RustElementVisitor() {
        var holder = wrap(_holder)

        override fun visitAttr(o: RustAttrElement) {
            holder.highlight(o, RustColor.ATTRIBUTE)
        }

        override fun visitLitExpr(o: RustLitExprElement) {
            // Re-highlight literals in attributes
            if (o.parent is RustMetaItemElement) {
                val literal = o.firstChild
                holder.highlight(literal, RustHighlighter.map(literal.elementType))
            }
        }

        override fun visitMacroInvocation(m: RustMacroInvocationElement) {
            holder.highlight(m, RustColor.MACRO)
        }

        override fun visitTypeParam(o: RustTypeParamElement) {
            holder.highlight(o.identifier, RustColor.TYPE_PARAMETER)

            val bounds = o.typeParamBounds?: return
            bounds.polyboundList.forEach {
                visitTraitRef(it.bound.traitRef ?: return)
            }
        }

        override fun visitTraitRef(o: RustTraitRefElement) = holder.highlight(o.path.identifier, RustColor.TRAIT)

        override fun visitPatBinding(o: RustPatBindingElement) {
            if (o.isMut) {
                holder.highlight(o.identifier, RustColor.MUT_BINDING)
            }
        }

        // Highlight the *reference* dependent on the target.
        override fun visitReference(o: RustReferenceElement) {
            val ref = o.reference.resolve() ?: return
            resolveColor(ref)?.let { holder.highlight(o, it) }
        }

        override fun visitPath(path: RustPathElement) {
            val ref = path.reference.resolve() ?: return
            resolveColor(ref)?.let { holder.highlight(path.identifier, it) }
        }

        override fun visitMethodCallExpr(o: RustMethodCallExprElement) {
            holder.highlight(o.identifier, RustColor.INSTANCE_METHOD)
        }

        override fun visitEnumItem(o: RustEnumItemElement)       = holder.highlight(o.identifier, RustColor.ENUM)
        override fun visitStructItem(o: RustStructItemElement)   = holder.highlight(o.identifier, RustColor.STRUCT)
        override fun visitTraitItem(o: RustTraitItemElement)     = holder.highlight(o.identifier, RustColor.TRAIT)
        override fun visitModDeclItem(o: RustModDeclItemElement) = holder.highlight(o.identifier, RustColor.MODULE)
        override fun visitModItem(o: RustModItemElement)         = holder.highlight(o.identifier, RustColor.MODULE)

        override fun visitFnItem(o: RustFnItemElement) {
            holder.highlight(o.identifier, RustColor.FUNCTION_DECLARATION)
        }

        override fun visitImplMethodMember(o: RustImplMethodMemberElement) {
            val color = if (o.isStatic) RustColor.STATIC_METHOD else RustColor.INSTANCE_METHOD
            holder.highlight(o.identifier, color)
        }

        override fun visitTraitMethodMember(o: RustTraitMethodMemberElement) {
            val color = if (o.isStatic) RustColor.STATIC_METHOD else RustColor.INSTANCE_METHOD
            holder.highlight(o.identifier, color)
        }

            // Capture the color of the element w/o mutating anything.
        private fun resolveColor(e: PsiElement): RustColor? {
            var outColor: RustColor? = null
            val prevHolder = holder
            this.holder = object : Highlighter {
                override fun highlight(element: PsiElement?, textAttributesKey: TextAttributesKey?) {
                    outColor = null;
                }

                override fun highlight(element: PsiElement?, color: RustColor?) {
                    assert(outColor == null, { "$e is not a leaf element, ambiguous color." })
                    outColor = color;
                }
            }
            e.accept(this)
            this.holder = prevHolder
            return outColor
        }

    })
}

interface Highlighter {
    fun highlight(element: PsiElement?, textAttributesKey: TextAttributesKey?);

    fun highlight(element: PsiElement?, color: RustColor?) {
        highlight(element, color?.textAttributesKey)
    }
}

fun wrap(holder: AnnotationHolder): Highlighter {
    return object: Highlighter{

        override fun highlight(element: PsiElement?, textAttributesKey: TextAttributesKey?) {
            if (element != null && textAttributesKey != null) {
                holder.createInfoAnnotation(element, null).textAttributes = textAttributesKey
            }
        }

    }
}
}
