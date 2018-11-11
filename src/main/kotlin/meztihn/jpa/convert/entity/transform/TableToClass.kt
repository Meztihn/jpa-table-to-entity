package meztihn.jpa.convert.entity.transform

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import meztihn.jpa.convert.entity.java.*
import meztihn.jpa.convert.entity.java.Constructor.*
import meztihn.jpa.convert.entity.java.Explicitness.*
import net.sf.jsqlparser.statement.create.table.ColumnDefinition
import net.sf.jsqlparser.statement.create.table.CreateTable
import java.math.BigDecimal
import javax.lang.model.element.Modifier.*
import javax.persistence.*
import kotlin.text.RegexOption.*

private val timeTypePattern = Regex("""(?<name>time\w*)\s*(?<args>\(.*\))?\s*(?<specs>.*)""", IGNORE_CASE)
private val spaces = Regex("""\s+""")

fun CreateTable.toClass(options: Options): TypeSpec = with(options) {
    fixTimeFieldsModel(columnDefinitions)
    val fields = columnDefinitions.map { it.toField(options) }
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

fun f() {

}

private fun fixTimeFieldsModel(columnDefinitions: List<ColumnDefinition>) {
    columnDefinitions
        .filter { it.colDataType.dataType.startsWith("time", ignoreCase = true) }
        .forEach { column ->
            timeTypePattern.find(column.colDataType.dataType)?.groups?.let { groups ->
                column.colDataType.dataType = groups["name"]!!.value
                groups["args"]?.let { arguments ->
                    column.colDataType.argumentsStringList = arguments.value.split(',').map { it.trim() }
                }
                groups["specs"]?.let { specifications ->
                    val elements = specifications.value.split(spaces)
                    column.columnSpecStrings = column.columnSpecStrings?.plus(elements) ?: elements
                }
            }
        }
}

fun ColumnDefinition.toField(options: Options): FieldSpec {
    val type = javaClassFor(colDataType.dataType).run { takeUnless { nullable } ?: boxed }
    val shouldUseJavaUtilDate = (options.dateTimePackage == DateTimePackage.util) and isTemporal(type)
    return FieldSpec.builder(
        type.takeUnless { shouldUseJavaUtilDate } ?: java.util.Date::class.java,
        columnName.toLowerCamelCase(),
        PRIVATE
    ).apply {
        toAnnotation(options.namesExplicitness).let { annotation ->
            if (annotation.members.isNotEmpty()) addAnnotation(annotation)
        }
        if (shouldUseJavaUtilDate) {
            addAnnotation(temporalAnnotation(temporalTypeOf(type)))
        }
    }.build()
}

fun ColumnDefinition.toAnnotation(nameExplicitness: Explicitness): AnnotationSpec {
    val type = javaClassFor(colDataType.dataType)
    return AnnotationSpec.builder(Column::class.java).apply {
        if (nameExplicitness == Explicit) add(Column::name, columnName)
        add(Column::nullable, nullable)
        if (type == String::class.java) {
            colDataType?.argumentsStringList
                ?.takeIf { it.isNotEmpty() }
                ?.first()
                ?.toInt()
                ?.let { length -> add(Column::length, length) }
        }
        if (type == BigDecimal::class.java) {
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
    get() = columnSpecStrings?.windowed(2)?.none {
        it.joinToString(" ").toLowerCase() == "not null"
    } ?: true

val Class<*>.boxed
    get() = kotlin.javaObjectType

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
