package meztihn.jpa.convert.entity.transform

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import meztihn.jpa.convert.entity.java.*
import meztihn.jpa.convert.entity.java.Constructor.Default
import meztihn.jpa.convert.entity.java.Constructor.Full
import meztihn.jpa.convert.entity.java.Explicitness.Explicit
import meztihn.jpa.convert.entity.sql.JpaType
import meztihn.jpa.convert.entity.sql.JpaType.*
import net.sf.jsqlparser.statement.create.table.ColumnDefinition
import net.sf.jsqlparser.statement.create.table.CreateTable
import java.math.BigDecimal
import java.util.*
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.persistence.*

fun CreateTable.toClass(options: Options): TypeSpec = with(options) {
    val fields = columnDefinitions.map { it.toField(options.namesExplicitness) }
    TypeSpec.classBuilder(table.name.toUpperCamelCase()).apply {
        addAnnotation(Entity::class.java)
        if (namesExplicitness == Explicit) addAnnotation(
            AnnotationSpec.builder(Table::class.java)
                .add(Table::schema, table.schemaName)
                .add(Table::name, table.name)
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

fun ColumnDefinition.toField(nameExplicitness: Explicitness): FieldSpec {
    val type = JpaType.parse(colDataType.dataType)
    val javaClass = type.javaClass.run { takeUnless { nullable } ?: boxed }
    return FieldSpec.builder(javaClass, columnName.toLowerCamelCase(), PRIVATE).apply {
        toAnnotation(nameExplicitness).let { annotation ->
            if (annotation.members.isNotEmpty()) addAnnotation(annotation)
        }
        if ((javaClass == Date::class.java) or (javaClass == Calendar::class.java)) {
            addAnnotation(temporalAnnotation(type.temporalType))
        }
    }.build()
}

fun ColumnDefinition.toAnnotation(nameExplicitness: Explicitness): AnnotationSpec {
    val type = JpaType.parse(colDataType.dataType)
    return AnnotationSpec.builder(Column::class.java).apply {
        if (nameExplicitness == Explicit) add(Column::name, columnName)
        add(Column::nullable, nullable)
        if (type.isWithLength) {
            colDataType?.argumentsStringList
                ?.takeIf { it.isNotEmpty() }
                ?.first()
                ?.toInt()
                ?.let { length -> add(Column::length, length) }
        }
        if (type.isWithPrecision) {
            colDataType?.argumentsStringList
                ?.takeIf { it.isNotEmpty() }
                ?.map { it.toInt() }
                ?.let { arguments ->
                    add(Column::precision, arguments[0])
                    if (arguments.size > 1) add(Column::scale, arguments[1])
                }
        }
    }.build()
}

val ColumnDefinition.nullable: Boolean
    get() = colDataType?.arrayData?.windowed(2)?.none {
        it.joinToString(" ").toLowerCase() == "not null"
    } ?: true

val JpaType.javaClass: Class<*>
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

val JpaType.temporalType: TemporalType
    get() = when (this) {
        DATE -> TemporalType.DATE
        TIME -> TemporalType.TIME
        TIMESTAMP -> TemporalType.TIMESTAMP
        else -> throw IllegalArgumentException("$this is not a temporal type")
    }

private fun temporalAnnotation(temporalType: TemporalType): AnnotationSpec {
    return AnnotationSpec.builder(Temporal::class.java)
        .add(Temporal::value, temporalType)
        .build()
}

private fun constructor(fields: List<FieldSpec>): MethodSpec {
    return MethodSpec.constructorBuilder().apply {
        addModifiers(PUBLIC)
        addParameters(fields.map { it.toParameter() })
        fields.forEach { addCode("this.$1L = $1L;\n", it.name) }
    }.build()
}
