package meztihn.jpa.convert.entity.transform

import meztihn.jpa.convert.entity.parse.ParseException
import java.math.BigDecimal
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.*

private val mapping: Map<String, Class<*>> = listOf(
    String::class to listOf("character", "char", "character varying", "varchar", "longvarchar", "text"),
    BigDecimal::class to listOf("numeric", "decimal"),
    Boolean::class to listOf("bit", "bool", "boolean"),
    Byte::class to listOf("tinyint"),
    Short::class to listOf("smallint", "int2", "smallserial", "serial2"),
    Int::class to listOf("integer", "int", "int4", "serial"),
    Long::class to listOf("bigint", "int8", "bigserial", "serial8"),
    Float::class to listOf("real", "float4"),
    Double::class to listOf("float", "float8", "double precision"),
    ByteArray::class to listOf("binary", "varbinary", "longvarbinary", "bytea"),
    UUID::class to listOf("uuid"),
    Date::class to listOf("date"),
    Time::class to listOf("time", "timetz"),
    Timestamp::class to listOf("timestamp", "timestamptz")
).flatMap { (kotlinClass, sqlTypes) ->
    sqlTypes.map { it to kotlinClass.java }
}.toMap()

fun javaClassFor(sqlTypeName: String): Class<*> =
    mapping[sqlTypeName.toLowerCase()] ?: throw ParseException("SQL type \"$sqlTypeName\" not found")