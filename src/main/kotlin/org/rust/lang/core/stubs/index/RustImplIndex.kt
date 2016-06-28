package org.rust.lang.core.stubs.index

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.rust.lang.core.psi.RustImplItemElement
import org.rust.lang.core.psi.RustTraitItemElement
import org.rust.lang.core.types.RustType
import org.rust.lang.core.types.util.resolvedType


class RustImplIndex : StringStubIndexExtension<RustImplItemElement>() {
    override fun getKey(): StubIndexKey<String, RustImplItemElement> = KEY

    companion object {
        val KEY: StubIndexKey<String, RustImplItemElement> =
            StubIndexKey.createIndexKey("com.intellij.psi.stubs.StringStubIndexExtension#StringStubIndexExtension")


        fun getImplForTraitAndType(trait: RustTraitItemElement, type: RustType): RustImplItemElement? {
            val project = trait.project
            val traitName = trait.name ?: return null
            val key = "$traitName@$type"
            return StubIndex.getElements(
                KEY, key, project, GlobalSearchScope.allScope(project), RustImplItemElement::class.java
            ).find { impl ->
               impl.traitRef?.path?.reference?.resolve() == trait && impl.type?.resolvedType == type
            }
        }
    }

}
