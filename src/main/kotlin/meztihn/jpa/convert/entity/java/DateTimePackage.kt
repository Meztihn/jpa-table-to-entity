package meztihn.jpa.convert.entity.java

@Suppress("EnumEntryName")
enum class DateTimePackage(val path: String) {
    sql("java.sql.*"),
    util("java.util.*"),
    time("java.time.*");

    companion object {
        fun fromPath(path: String): DateTimePackage = values().first { it.path == path }
    }
}
