package com.example.mynetwork.extension

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private val calendar = Calendar.getInstance()

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

fun View.pickDate(editText: EditText, context: Context) {
    val datePicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = monthOfYear
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        editText.setText(
            SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                .format(calendar.time)
        )
    }
    DatePickerDialog(
        context, datePicker,
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
            SimpleDateFormat("HH-mm", Locale.ROOT)
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