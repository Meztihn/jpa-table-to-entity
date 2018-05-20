package meztihn.jpa.convert.entity.java

import com.squareup.javapoet.*
import java.math.BigDecimal
import java.util.*

object BasicTypeName {
    val STRING: TypeName = TypeName.get(String::class.java)
    val BIG_DECIMAL: TypeName = TypeName.get(BigDecimal::class.java)
    val BYTE_ARRAY: TypeName = ArrayTypeName.get(Byte::class.java)
    val DATE: TypeName = TypeName.get(Date::class.java)
    val CALENDAR: TypeName = TypeName.get(Calendar::class.java)
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