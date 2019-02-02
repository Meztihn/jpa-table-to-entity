package meztihn.jpa.convert.entity.transform

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import meztihn.jpa.convert.entity.java.Constructor
import meztihn.jpa.convert.entity.java.DateTimePackage
import meztihn.jpa.convert.entity.java.Explicitness
import meztihn.jpa.convert.entity.java.Mutability
import net.sf.jsqlparser.statement.create.table.ColDataType
import net.sf.jsqlparser.statement.create.table.ColumnDefinition
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import javax.lang.model.element.Modifier

internal class ColumnToFieldTest : Spek({
    describe("a field") {
        val columnName = "column_name"
        val field = field(name = columnName)

        it("is private") {
            assertThat(field.hasModifier(Modifier.PRIVATE), equalTo(true))
        }

        it("name is in lower camel case") {
            assertThat(field.name, equalTo(columnName.toLowerCamelCase()))
        }
    }

    val typesMapping = mapOf(
        "boolean" to Boolean::class.java,
        "tinyint" to Byte::class.java,
        "smallint" to Short::class.java,
        "int" to Int::class.java,
        "bigint" to Long::class.java,
        "real" to Float::class.java,
        "float" to Double::class.java,
        "varchar" to String::class.java,
        "text" to String::class.java
    )

    typesMapping.forEach { columnTypeName, javaClass ->
        describe("a field type") {
            val isPrimitiveOrBoxed = javaClass.kotlin.javaPrimitiveType != null
            if (isPrimitiveOrBoxed) {
                it("is primitive for not null column") {
                    val field = field(typeName = columnTypeName, nullable = false)
                    val primitiveTypeName = TypeName.get(javaClass.kotlin.javaPrimitiveType)
                    assertThat(field.type, equalTo(primitiveTypeName))
                }
                it("is boxed for nullable column") {
                    val field = field(typeName = columnTypeName, nullable = true)
                    val boxedTypeName = TypeName.get(javaClass.kotlin.javaObjectType)
                    assertThat(field.type, equalTo(boxedTypeName))
                }
            } else {
                it("matches column type") {
                    val field = field(typeName = columnTypeName)
                    assertThat(field.type, equalTo(TypeName.get(javaClass)))
                }
            }
        }
    }
})

private val defaultOptions =
    Options(Mutability.Immutable, Constructor.Default, Explicitness.Explicit, DateTimePackage.sql, "")

private fun field(name: String = "name", typeName: String = "text", nullable: Boolean = true): FieldSpec {
    val column = ColumnDefinition().apply {
        columnName = name
        colDataType = ColDataType().apply {
            dataType = typeName
            if (!nullable) {
                columnSpecStrings = listOf("not", "null")
            }
        }
    }
    return column.toField(defaultOptions)
}