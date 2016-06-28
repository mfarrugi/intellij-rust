package org.rust.lang.core.stubs.elements


import com.intellij.psi.stubs.*
import org.rust.lang.core.psi.RustImplItemElement
import org.rust.lang.core.psi.impl.RustImplItemElementImpl
import org.rust.lang.core.psi.impl.mixin.baseTypeName
import org.rust.lang.core.psi.impl.mixin.traitName
import org.rust.lang.core.stubs.RustElementStub
import org.rust.lang.core.stubs.RustStubElementType
import org.rust.lang.core.stubs.index.RustImplIndex

object RustImplItemStubElementType : RustStubElementType<RustImplItemElementStub, RustImplItemElement>("IMPL_ITEM") {
    override fun createStub(psi: RustImplItemElement, parentStub: StubElement<*>?): RustImplItemElementStub =
        RustImplItemElementStub(parentStub, this, psi.traitName, psi.baseTypeName)

    override fun createPsi(stub: RustImplItemElementStub): RustImplItemElement =
        RustImplItemElementImpl(stub, this)

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RustImplItemElementStub =
        RustImplItemElementStub(parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string)

    override fun serialize(stub: RustImplItemElementStub, dataStream: StubOutputStream) = with(dataStream) {
        writeName(stub.traitName)
        writeName(stub.typeName)
    }

    override fun indexStub(stub: RustImplItemElementStub, sink: IndexSink) {
        val traitName = stub.traitName ?: return
        val typeName = stub.typeName ?: return
        sink.occurrence(RustImplIndex.KEY, "$traitName@$typeName")
    }

}


class RustImplItemElementStub : RustElementStub<RustImplItemElement> {
    val traitName: String?
    val typeName: String?

    constructor(parent: StubElement<*>?, elementType: IStubElementType<*, *>, traitName: String?, typeName: String?)
    : super(parent, elementType) {
        this.traitName = traitName
        this.typeName = typeName
    }

}
