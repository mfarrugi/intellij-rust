package org.rust.lang.core.psi.impl.mixin

import com.intellij.lang.ASTNode
import org.rust.lang.core.psi.RustNamedElement
import org.rust.lang.core.psi.RustParameterElement
import org.rust.lang.core.psi.impl.RustCompositeElementImpl
import org.rust.lang.core.psi.util.boundElements

abstract class RustParameterImplMixin(node: ASTNode) : RustCompositeElementImpl(node), RustParameterElement {
    override val boundElements: Collection<RustNamedElement>
        get() = pat?.boundElements.orEmpty()
}
