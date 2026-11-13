package com.gencnis.enson.ui.common

import com.gencnis.enson.model.TrackType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone

class DatesTest {

    private val originalZone = TimeZone.getDefault()

    @After
    fun restoreZone() {
        TimeZone.setDefault(originalZone)
    }

    @Test
    fun currentEpochDay_followsLocalCalendarDate() {
        // Far ahead of and far behind UTC, the local date can differ
        // from the UTC date; the count must follow the local one.
        listOf(
            "Pacific/Kiritimati",
            "Pacific/Pago_Pago",
            "Europe/Istanbul"
        ).forEach { zone ->
            TimeZone.setDefault(TimeZone.getTimeZone(zone))

            assertEquals(
                zone,
                LocalDate.now(ZoneId.of(zone)).toEpochDay(),
                currentEpochDay()
            )
        }
    }

    @Test
    fun formatEpochDay_isTurkish() {
        assertEquals(
            "1 Ocak 1970",
            formatEpochDay(0)
        )

        assertEquals(
            "13 Kasım 2026, Cuma",
            formatEpochDayWithWeekday(
                LocalDate.of(2026, 11, 13).toEpochDay()
            )
        )
    }

    @Test
    fun millisUntilNextLocalDay_isWithinADay() {
        val millis = millisUntilNextLocalDay()

        assertTrue(millis > 0)
        // A DST change can make one local day 25 hours long.
        assertTrue(millis <= 25 * 60 * 60 * 1000L)
    }

    @Test
    fun scheduleStatus_countsDownAndFlagsDueDays() {
        val upcoming = scheduleStatus(
            TrackType.NORMAL,
            repeatIntervalDays = 7,
            elapsedDays = 4
        )

        assertEquals("3 gün kaldı", upcoming?.text)
        assertFalse(upcoming!!.isDue)

        val today = scheduleStatus(
            TrackType.NORMAL,
            repeatIntervalDays = 7,
            elapsedDays = 7
        )

        assertEquals("Bugün zamanı", today?.text)
        assertTrue(today!!.isDue)

        val late = scheduleStatus(
            TrackType.NORMAL,
            repeatIntervalDays = 7,
            elapsedDays = 9
        )

        assertEquals("2 gün gecikti", late?.text)
        assertTrue(late!!.isDue)
    }

    @Test
    fun scheduleStatus_isAbsentWithoutIntervalRecordOrForPeriods() {
        assertNull(
            scheduleStatus(
                TrackType.NORMAL,
                repeatIntervalDays = null,
                elapsedDays = 3
            )
        )

        assertNull(
            scheduleStatus(
                TrackType.NORMAL,
                repeatIntervalDays = 7,
                elapsedDays = null
            )
        )

        assertNull(
            scheduleStatus(
                TrackType.PERIOD_START,
                repeatIntervalDays = 28,
                elapsedDays = 40
            )
        )
    }
}
