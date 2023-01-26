package org.ppfc.api.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Long.toBoolean() = this == 1L

fun Boolean.toLong() = if (this) 1L else 0L

fun validateDateFormat(pattern: String, date: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern(pattern)

    return try {
        LocalDate.parse(date, formatter)
        true
    } catch (e: DateTimeParseException) {
        println(e.message)
        false
    }
}