package org.rust.lang.core.types

import org.rust.lang.core.psi.*
import org.rust.lang.core.stubs.index.RustImplIndex
import org.rust.lang.core.types.util.resolvedType

abstract class RustStructOrEnumTypeBase(struct: RustStructOrEnumItemElement) : RustType {

    override val inherentImpls: Collection<RustImplItemElement> by lazy {
        struct.containingMod
            ?.impls.orEmpty()
            .filter { it.type?.resolvedType == this }
    }

    override fun implFor(trait: RustTraitItemElement): RustImplItemElement? =
        RustImplIndex.getImplForTraitAndType(trait, this)

}
