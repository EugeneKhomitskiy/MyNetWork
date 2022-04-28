package com.example.mynetwork.extension

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private val calendar = Calendar.getInstance()

fun formatToDate(value: String?): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.ROOT)
        .withZone(ZoneId.systemDefault())

    return formatter.format(Instant.parse(value))
}

fun formatToInstant(value: String): String {
    return if (value != " ") {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(value)
        val formatter = DateTimeFormatter.ISO_INSTANT

        formatter.format(date?.toInstant())
    } else "2021-08-17T16:46:58.887547Z"
}

fun View.pickDate(editText: EditText?, context: Context?) {
    val datePicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = monthOfYear
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        editText?.setText(
            SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                .format(calendar.time)
        )
    }
    DatePickerDialog(
        context!!, datePicker,
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )
        .show()
}

fun View.pickTime(editText: EditText, context: Context) {
    val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        editText.setText(
            SimpleDateFormat("HH:mm", Locale.ROOT)
                .format(calendar.time)
        )
    }
    TimePickerDialog(
        context, timePicker,
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE), true
    )
        .show()
}

fun dateToEpochSec(str: String?): Long? {
    return if (str.isNullOrBlank()) null else LocalDate.parse(str)
        .atStartOfDay(ZoneId.of("Europe/Moscow")).toEpochSecond()
}

@SuppressLint("SimpleDateFormat")
fun epochSecToDate(second: Long): String {
    val date = Date(second * 1000)
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(date)
}