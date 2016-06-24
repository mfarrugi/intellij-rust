package org.rust.lang.core.types.visitors

import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.*
import org.rust.lang.core.psi.util.contains
import org.rust.lang.core.psi.util.fields
import org.rust.lang.core.psi.util.indexOf
import org.rust.lang.core.psi.util.pathTo
import org.rust.lang.core.types.*
import org.rust.lang.core.types.util.resolvedType


object RustTypeInferenceEngine {

    fun inferPatBindingTypeFrom(binding: RustPatBindingElement, pat: RustPatElement, patType: RustType): RustType {
        check(pat.contains(binding))

        return patType.accept(RustTypeInferencingVisitor(binding, pat))
    }

}

sealed class PatternPath {
    object End: PatternPath()

    class TupleField(val idx: Int, val next: PatternPath): PatternPath()

    class StructField(
        val pathExpr: RustPathExprElement,
        val fieldName: String,
        val next: PatternPath
    ): PatternPath()

    class TupleStructField(
        val pathExpr: RustPathExprElement,
        val fieldIdx: Int,
        val next: PatternPath
    ): PatternPath()

    class Ref(val mut: Boolean, val next: PatternPath): PatternPath()

    companion object {
        fun devise(binding: RustPatBindingElement, pat: RustPatElement): PatternPath {
            var result: PatternPath? = null

            var previous: PsiElement = binding
            for (element in binding.pathTo(pat).drop(1)) {
                if (element is RustPatIdentElement || element is RustPatFieldElement && element.patBinding == binding) {
                    check(result == null)
                    result = End
                }

                if (element is RustPatTupElement) {
                    result = TupleField(element.indexOf(previous as RustPatElement), result!!)
                }

                if (element is RustPatStructElement) {
                    val field = previous as RustPatFieldElement
                    val fieldName = field.patBinding?.let { it.identifier.text } ?: field.identifier?.text ?: ""
                    result = StructField(element.pathExpr, fieldName, result!!)
                }

                if (element is RustPatEnumElement) {
                    result = TupleStructField(element.pathExpr, element.patList.indexOf(previous as RustPatElement), result!!)
                }

                if (element is RustPatRefElement) {
                    result = Ref(element.mut != null, result!!)
                }

                previous = element
            }

            return result!!
        }
    }
}

@Suppress("IfNullToElvis")
private class RustTypeInferencingVisitor(
    binding: RustPatBindingElement,
    pat: RustPatElement
) : RustTypeVisitor<RustType> {

    var path: PatternPath = PatternPath.devise(binding, pat)

    override fun visitUnitType(type: RustUnitType): RustType {
        return if (path is PatternPath.End) type else RustUnknownType
    }

    override fun visitTupleType(type: RustTupleType): RustType {
        val tip = path
        if (tip is PatternPath.End)
            return type

        if (tip is PatternPath.TupleField) {
            path = tip.next

            return type[tip.idx].accept(this)
        }

        return RustUnknownType
    }

    override fun visitStruct(type: RustStructType): RustType {
        val tip = path
        if (tip is PatternPath.End)
            return type

        if (tip is PatternPath.StructField) {
            if (tip.pathExpr.resolvedType != type) {
                return RustUnknownType
            }
            val fieldDecl = type.struct.fields.find { it.name == tip.fieldName }
            if (fieldDecl == null)
                return RustUnknownType

            path = tip.next
            return fieldDecl.type.resolvedType.accept(this)
        }

        return RustUnknownType
    }

    override fun visitEnum(type: RustEnumType): RustType {
        val tip = path
        if (tip is PatternPath.End)
            return type

        if (tip is PatternPath.TupleStructField) {
            if (tip.pathExpr.resolvedType != type) {
                return RustUnknownType
            }
            val variant = tip.pathExpr.path.reference.resolve() as? RustEnumVariantElement
            val fieldDecl = variant?.enumStructArgs?.fieldDeclList.orEmpty().getOrNull(tip.fieldIdx)
                ?: return RustUnknownType

            path = tip.next
            return fieldDecl.type.resolvedType.accept(this)
        }
        //TODO: if (tip is PatternPath.StructField) {

        return RustUnknownType
    }

    override fun visitFunctionType(type: RustFunctionType): RustType {
        val tip = path
        if (tip is PatternPath.End)
            return type

        return RustUnknownType
    }

    override fun visitInteger(type: RustIntegerType): RustType {
        val tip = path
        if (tip is PatternPath.End)
            return type

        return RustUnknownType
    }

    override fun visitReference(type: RustReferenceType): RustType {
        val tip = path
        if (tip is PatternPath.End)
            return type

        if (tip is PatternPath.Ref) {
            if (tip.mut  != type.mutable)
                return RustUnknownType

            path = tip.next
            return type.referenced.accept(this)
        }

        return RustUnknownType
    }

    override fun visitUnknown(type: RustUnknownType): RustType {
        return RustUnknownType
    }

}

