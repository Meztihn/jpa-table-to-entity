package meztihn.jpa.convert.entity.sql

import meztihn.jpa.convert.entity.parse.ParseException

data class Table(val name: String, val columns: List<Column>) {
    data class Column(val name: String, val type: Type, val nullable: Boolean = true) {
        constructor(name: String, typeName: String, nullable: Boolean = true) : this(name, Type.parse(typeName), nullable)

        enum class Type(val sqlName: String) {
            CHARACTER("CHARACTER"),
            VARCHAR("VARCHAR"),
            LONGVARCHAR("LONGVARCHAR"),
            NUMERIC("NUMERIC"),
            DECIMAL("DECIMAL"),
            BIT("BIT"),
            TINYINT("TINYINT"),
            SMALLINT("SMALLINT"),
            INTEGER("INTEGER"),
            BIGINT("BIGINT"),
            REAL("REAL"),
            FLOAT("FLOAT"),
            DOUBLE_PRECISION("DOUBLE PRECISION"),
            BINARY("BINARY"),
            VARBINARY("VARBINARY"),
            LONGVARBINARY("LONGVARBINARY"),
            DATE("DATE"),
            TIMESTAMP("TIMESTAMP"), // Do not put it lower than time because they have same prefix
            TIME("TIME");

            companion object {
                fun parse(type: String): Type {
                    val typeName = type.toUpperCase().trim()
                    return values().firstOrNull {
                        typeName.startsWith(it.sqlName)
                    } ?: throw ParseException("Unrecognized sql type: $type")
                }
            }
        }
    }
}