package meztihn.jpa.convert.entity.transform

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import meztihn.jpa.convert.entity.java.Constructor.Default
import meztihn.jpa.convert.entity.java.Constructor.Full
import meztihn.jpa.convert.entity.java.Explicitness
import meztihn.jpa.convert.entity.java.Explicitness.Explicit
import meztihn.jpa.convert.entity.java.PropertySpec
import meztihn.jpa.convert.entity.java.addProperties
import meztihn.jpa.convert.entity.java.toParameter
import meztihn.jpa.convert.entity.sql.Table
import meztihn.jpa.convert.entity.sql.Table.Column.Type.*
import java.math.BigDecimal
import java.util.*
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Temporal
import javax.persistence.TemporalType

fun Table.toClass(options: Options): TypeSpec = with(options) {
    val fields = columns.map { it.toField(namesExplicitness) }
    TypeSpec.classBuilder(name.toUpperCamelCase()).apply {
        addAnnotation(Entity::class.java)
        if (namesExplicitness == Explicit) addAnnotation(
            AnnotationSpec.builder(javax.persistence.Table::class.java)
                .addMember("name", "\$S", name)
                .build()
        )
        when (constructor) {
            Full -> addMethod(constructor(fields))
            Default -> Unit
        }
        addModifiers(PUBLIC)
        addProperties(fields.map { PropertySpec(it, mutability) })
    }.build()
}

fun Table.Column.toField(nameExplicitness: Explicitness): FieldSpec {
    val javaClass = type.javaClass.run { takeUnless { nullable } ?: boxed }
    return FieldSpec.builder(javaClass, name.toLowerCamelCase(), PRIVATE).apply {
        AnnotationSpec.builder(Column::class.java).apply {
            if (nameExplicitness == Explicit) addMember("name", "\$S", name)
            if (nullable.not()) addMember("nullable", "\$L", false)
        }.build().takeIf { it.members.isNotEmpty() }?.let { addAnnotation(it) }
        if ((javaClass == Date::class.java) or (javaClass == Calendar::class.java)) {
            addAnnotation(temporalAnnotation(type.temporalType))
        }
    }.build()
}

val Table.Column.Type.javaClass: Class<*>
    get() = when (this) {
        CHARACTER -> String::class.java
        VARCHAR -> String::class.java
        LONGVARCHAR -> String::class.java
        NUMERIC -> BigDecimal::class.java
        DECIMAL -> BigDecimal::class.java
        BIT -> Boolean::class.java
        TINYINT -> Byte::class.java
        SMALLINT -> Short::class.java
        INTEGER -> Int::class.java
        BIGINT -> Long::class.java
        REAL -> Float::class.java
        FLOAT -> Double::class.java
        DOUBLE_PRECISION -> Double::class.java
        BINARY -> Array<Byte>::class.java
        VARBINARY -> Array<Byte>::class.java
        LONGVARBINARY -> Array<Byte>::class.java
        DATE -> Date::class.java
        TIME -> Date::class.java
        TIMESTAMP -> Date::class.java
    }

val Class<*>.boxed
    get() = kotlin.javaObjectType

val Table.Column.Type.temporalType: TemporalType
    get() = when (this) {
        DATE -> TemporalType.DATE
        TIME -> TemporalType.TIME
        TIMESTAMP -> TemporalType.TIMESTAMP
        else -> throw IllegalArgumentException("$this is not a temporal type")
    }

private fun temporalAnnotation(temporalType: TemporalType): AnnotationSpec = AnnotationSpec.builder(Temporal::class.java)
    .addMember("value", "\$L", temporalType)
    .build()

private fun constructor(fields: List<FieldSpec>): MethodSpec {
    return MethodSpec.constructorBuilder().apply {
        addModifiers(PUBLIC)
        addParameters(fields.map { it.toParameter() })
        fields.forEach { addCode("this.$1L = $1L;\n", it.name) }
    }.build()
}
