package meztihn.jpa.convert.entity.java

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName.VOID
import meztihn.jpa.convert.entity.java.Mutability.Immutable
import meztihn.jpa.convert.entity.java.Mutability.Mutable
import javax.lang.model.element.Modifier.PUBLIC

sealed class PropertySpec(val field: FieldSpec) {
    companion object {
        operator fun invoke(field: FieldSpec, mutability: Mutability): PropertySpec {
            return when (mutability) {
                Immutable -> ReadablePropertySpec(field)
                Mutable -> WritablePropertySpec(field)
            }
        }
    }

}

open class ReadablePropertySpec(field: FieldSpec) : PropertySpec(field) {
    val getter: MethodSpec = with(field) {
        MethodSpec.methodBuilder("get${name.capitalize()}")
            .addModifiers(PUBLIC)
            .returns(type)
            .addCode("return this.\$L;\n", name)
            .build()
    }
}

class WritablePropertySpec(field: FieldSpec) : ReadablePropertySpec(field) {
    val setter: MethodSpec = with(field) {
        MethodSpec.methodBuilder("set${name.capitalize()}")
            .addModifiers(PUBLIC)
            .returns(VOID)
            .addParameter(type, name)
            .addCode("this.\$1L = \$1L;\n", name)
            .build()
    }
}

