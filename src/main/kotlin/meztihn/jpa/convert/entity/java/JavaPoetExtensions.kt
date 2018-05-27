package meztihn.jpa.convert.entity.java

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaGetter

inline fun <reified Type> AnnotationSpec.Builder.add(
    property: KProperty1<*, Type>,
    value: Type
): AnnotationSpec.Builder = apply {
    if ((value != null) and (value != property.javaGetter!!.defaultValue)) {
        when (value) {
            is String -> addMember(property.name, "\$S", value)
            is Enum<*> -> addMember(property.name, "\$T.\$L", Type::class.java, value)
            else -> addMember(property.name, "\$L", value)
        }
    }
}

fun TypeSpec.Builder.addProperty(propertySpec: PropertySpec): TypeSpec.Builder = apply {
    addField(propertySpec.field)
    if (propertySpec is ReadablePropertySpec) addMethod(propertySpec.getter)
    if (propertySpec is WritablePropertySpec) addMethod(propertySpec.setter)
}

fun TypeSpec.Builder.addProperties(propertySpecs: List<PropertySpec>): TypeSpec.Builder = apply {
    propertySpecs.forEach { addProperty(it) }
}

fun FieldSpec.toParameter(): ParameterSpec = ParameterSpec.builder(type, name).build()