package meztihn.jpa.convert.entity.parse

class ParseException(override val message: String) : Exception(message, null, true, false)