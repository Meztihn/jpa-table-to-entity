package meztihn.jpa.convert.entity.parse

import meztihn.jpa.convert.entity.sql.Table

private val tableRegex = Regex("create.*?\\s+table.*?\\s+(?<name>[\\w_]+)\\s*\\((?<columns>.+)\\)", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
private val columnRegex = Regex("(?<name>[\\w_]+)\\s+(?<type>[\\w\\s]+)")

fun parseTable(tableDefinition: String): Table {
    val match = tableRegex.find(tableDefinition) ?: throw ParseException("Incorrect table definition: $tableDefinition")
    val (name, columnDefinitions) = match.destructured
    val columns = columnDefinitions.split(',')
        .filter { isNotAConstraint(it) }
        .map { parseColumn(it.trim()) }
    return Table(name, columns)
}

private fun isNotAConstraint(it: String): Boolean {
    val normalized = it.toUpperCase()
    val constraintPrefixes = listOf(
        "CONSTRAINT",
        "CHECK",
        "UNIQUE",
        "PRIMARY KEY",
        "EXCLUDE",
        "FOREIGN KEY",
        "DEFERRABLE",
        "NOT DEFERRABLE",
        "INITIALLY"
    )
    return constraintPrefixes.none { prefix -> normalized.startsWith(prefix) }
}

fun parseColumn(columnDefinition: String): Table.Column {
    val match = columnRegex.find(columnDefinition) ?: throw ParseException("Incorrect column definition: $columnDefinition")
    val (name, type) = match.destructured
    val nullable = columnDefinition.contains("not null", true).not()
    return Table.Column(name, type, nullable)
}