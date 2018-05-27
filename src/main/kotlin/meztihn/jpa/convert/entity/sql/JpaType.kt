package meztihn.jpa.convert.entity.sql

import meztihn.jpa.convert.entity.parse.ParseException

enum class JpaType(val sqlName: String) {
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
        private val withLength: Set<JpaType> = setOf(BIT, CHARACTER, VARCHAR)

        fun parse(type: String): JpaType {
            val typeName = type.toUpperCase().trim()
            return values().firstOrNull {
                typeName.startsWith(it.sqlName)
            } ?: throw ParseException("Unrecognized sql type: $type")
        }
    }

    val isWithPrecision: Boolean
        get() = this == NUMERIC

    val isWithLength: Boolean
        get() = withLength.contains(this)
}