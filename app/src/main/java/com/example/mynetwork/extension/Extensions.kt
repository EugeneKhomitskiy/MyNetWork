package com.example.mynetwork.extension

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@SuppressLint("NewApi")
fun formatToDate(value: String): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.ROOT)
        .withZone(ZoneId.systemDefault())

    return formatter.format(Instant.parse(value))
}

@SuppressLint("NewApi")
fun formatToInstant(value: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault()).parse(value)
    val formatter = DateTimeFormatter.ISO_INSTANT

    return formatter.format(date?.toInstant())
}