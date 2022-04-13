package com.example.mynetwork.extension

import android.annotation.SuppressLint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@SuppressLint("NewApi")
fun formatDate(value: String): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.ROOT)
        .withZone(ZoneId.systemDefault())

    return formatter.format(Instant.parse(value))
}