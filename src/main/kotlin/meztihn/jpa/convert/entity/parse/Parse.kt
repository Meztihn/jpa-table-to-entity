package meztihn.jpa.convert.entity.parse

import net.sf.jsqlparser.parser.CCJSqlParserUtil.parseStatements
import net.sf.jsqlparser.statement.create.table.CreateTable

private const val delimiter = ";"

fun parseCreateTable(tableDefinition: String): CreateTable {
    val (_, other) = tableDefinition.split(delimiter).partition {
        it.trimStart().startsWith("comment on", ignoreCase = true)
    }
    val statements = parseStatements(other.joinToString(delimiter)).statements.filter {
        it is CreateTable
    }.map {
        it as CreateTable
    }
    return when (statements.size) {
        0 -> throw ParseException("Create table statement not found")
        1 -> statements.first()
        else -> throw ParseException("Too many create table statements")
    }
}