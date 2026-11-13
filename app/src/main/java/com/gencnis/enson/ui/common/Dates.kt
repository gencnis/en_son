package com.gencnis.enson.ui.common

import com.gencnis.enson.model.TrackType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Dates are stored as "epoch days": the device's local calendar date,
// counted as whole days since 1970-01-01. Differences between them are
// calendar-day differences, not 24-hour blocks.

const val MILLIS_PER_DAY =
    86_400_000L

private val turkish =
    Locale.forLanguageTag("tr-TR")

fun currentEpochDay(): Long {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    )

    formatter.timeZone =
        TimeZone.getDefault()

    val todayText =
        formatter.format(Date())

    val utcFormatter =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

    utcFormatter.timeZone =
        TimeZone.getTimeZone("UTC")

    val date =
        utcFormatter.parse(todayText)
            ?: return 0L

    return date.time / MILLIS_PER_DAY
}

fun parseDateToEpochDay(
    text: String
): Long? {
    val formatter =
        SimpleDateFormat(
            "dd.MM.yyyy",
            turkish
        )

    formatter.isLenient = false

    formatter.timeZone =
        TimeZone.getTimeZone("UTC")

    return try {
        val date =
            formatter.parse(text.trim())
                ?: return null

        date.time / MILLIS_PER_DAY

    } catch (_: ParseException) {
        null
    }
}

fun formatEpochDay(
    epochDay: Long
): String {
    val formatter =
        SimpleDateFormat(
            "d MMMM yyyy",
            turkish
        )

    formatter.timeZone =
        TimeZone.getTimeZone("UTC")

    return formatter.format(
        Date(
            epochDay * MILLIS_PER_DAY
        )
    )
}

fun scheduleText(
    type: TrackType,
    repeatIntervalDays: Int?,
    elapsedDays: Int?
): String? {

    if (type == TrackType.PERIOD_START) {
        return null
    }

    if (
        repeatIntervalDays == null ||
        elapsedDays == null
    ) {
        return null
    }

    val remaining =
        repeatIntervalDays -
                elapsedDays

    return when {
        remaining > 0 ->
            "$remaining gün kaldı"

        remaining == 0 ->
            "Bugün zamanı"

        else ->
            "${-remaining} gün gecikti"
    }
}
