package com.gencnis.enson.ui.common

import com.gencnis.enson.model.TrackType
import java.text.SimpleDateFormat
import java.util.Calendar
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

// "2026-11-13", e.g. for file names.
fun formatEpochDayIso(
    epochDay: Long
): String {
    val formatter =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

    formatter.timeZone =
        TimeZone.getTimeZone("UTC")

    return formatter.format(
        Date(
            epochDay * MILLIS_PER_DAY
        )
    )
}

// Header text such as "13 Kasım 2026, Cuma".
fun formatEpochDayWithWeekday(
    epochDay: Long
): String {
    val formatter =
        SimpleDateFormat(
            "d MMMM yyyy, EEEE",
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

fun millisUntilNextLocalDay(): Long {
    val nextMidnight =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

    return (
            nextMidnight.timeInMillis -
                    System.currentTimeMillis()
            ).coerceAtLeast(1_000L)
}

data class ScheduleStatus(
    val text: String,
    // Due today or late.
    val isDue: Boolean
)

fun scheduleStatus(
    type: TrackType,
    repeatIntervalDays: Int?,
    elapsedDays: Int?
): ScheduleStatus? {

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

    val text = when {
        remaining > 0 ->
            "$remaining gün kaldı"

        remaining == 0 ->
            "Bugün zamanı"

        else ->
            "${-remaining} gün gecikti"
    }

    return ScheduleStatus(
        text = text,
        isDue = remaining <= 0
    )
}
