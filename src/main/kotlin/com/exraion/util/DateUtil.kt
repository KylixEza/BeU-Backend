package com.exraion.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun createTimeStamp(format: DateFormat): String = run {
    val date = java.util.Date()
    val formatter = SimpleDateFormat(format.format)
    formatter.timeZone = java.util.TimeZone.getTimeZone("Asia/Jakarta")
    formatter.format(date)
}

fun List<String>.sortDate(): List<String> = run {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    sortedByDescending { LocalDate.parse(it, formatter) }
}

infix fun String.gapBetween(date: String) = run {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date1 = dateFormat.parse(this)
    val date2 = dateFormat.parse(date)

    val diffInMillis = abs(date2.time - date1.time)
    TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
}

enum class DateFormat(val format: String) {
    DATE_TIME("yyyy-MM-dd'T'HH:mm"),
    DATE("yyyy-MM-dd"),
}