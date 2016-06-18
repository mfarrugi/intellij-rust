package org.rust.ide.colors

import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.rust.ide.highlight.syntax.RustHighlighter
import org.rust.ide.icons.RustIcons
import org.rust.ide.utils.loadCodeSampleResource

class RustColorSettingsPage : ColorSettingsPage {
    private val ATTRS = RustColor.values().map { it.attributesDescriptor }.toTypedArray()

    // This tags should be kept in sync with RustHighlightingAnnotator highlighting logic
    // @TODO generate from RustColor
    // @TODO Figure out how to namespace menu elements a la
    //       https://github.com/JetBrains/kotlin/blob/8b30e7ef4e48494ba245b441a5b23142f1d6ae33/idea/idea-analysis/src/org/jetbrains/kotlin/idea/KotlinBundle.properties
    private val ANNOTATOR_TAGS = mapOf(
        "STRUCT" to RustColor.STRUCT,
        "ENUM" to RustColor.ENUM,
        "TRAIT" to RustColor.TRAIT,
        "attribute" to RustColor.ATTRIBUTE,
        "macro" to RustColor.MACRO,
        "type-parameter" to RustColor.TYPE_PARAMETER,
        "mut-binding" to RustColor.MUT_BINDING,
        "function-decl" to RustColor.FUNCTION_DECLARATION,
        "instance-method-decl" to RustColor.INSTANCE_METHOD,
        "static-method-decl" to RustColor.STATIC_METHOD
    ).mapValues { it.value.textAttributesKey }

    private val DEMO_TEXT by lazy {
        loadCodeSampleResource("org/rust/ide/colors/highlighterDemoText.rs")
    }

    override fun getDisplayName() = "Rust"
    override fun getIcon() = RustIcons.RUST
    override fun getAttributeDescriptors() = ATTRS
    override fun getColorDescriptors() = ColorDescriptor.EMPTY_ARRAY
    override fun getHighlighter() = RustHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap() = ANNOTATOR_TAGS
    override fun getDemoText() = DEMO_TEXT
}
