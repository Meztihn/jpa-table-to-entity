package meztihn.jpa.convert.entity.transform

fun String.toUpperCamelCase(): String = split("_").joinToString("") { it.capitalize() }
fun String.toLowerCamelCase(): String = toUpperCamelCase().decapitalize()