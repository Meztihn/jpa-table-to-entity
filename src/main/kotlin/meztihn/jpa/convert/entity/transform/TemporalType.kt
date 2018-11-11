package meztihn.jpa.convert.entity.transform

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import javax.persistence.TemporalType
import javax.persistence.TemporalType.*

private val temporalType: Map<Class<*>, TemporalType> = mapOf(
    Date::class.java to DATE,
    Time::class.java to TIME,
    Timestamp::class.java to TIMESTAMP
)

fun isTemporal(type: Class<*>): Boolean = type in temporalType

fun temporalTypeOf(type: Class<*>): TemporalType =
    temporalType[type] ?: throw IllegalArgumentException("$type is not a temporal type")