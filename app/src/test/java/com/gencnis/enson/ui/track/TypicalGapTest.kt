package com.gencnis.enson.ui.track

import com.gencnis.enson.data.local.TrackRecordEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TypicalGapTest {

    private fun records(vararg days: Long) =
        days.mapIndexed { index, day ->
            TrackRecordEntity(
                id = index.toLong(),
                trackId = 1,
                dateEpochDay = day
            )
        }

    @Test
    fun needsAtLeastThreeEntries() {
        assertNull(typicalGapDays(records()))
        assertNull(typicalGapDays(records(10)))
        assertNull(typicalGapDays(records(10, 17)))
    }

    @Test
    fun usesMedianGapRegardlessOfOrder() {
        // Gaps 7, 7, 30: one long break shouldn't skew "usually".
        assertEquals(
            7,
            typicalGapDays(records(44, 0, 7, 14))
        )
    }

    @Test
    fun dailyHabit() {
        assertEquals(
            1,
            typicalGapDays(records(1, 2, 3, 4))
        )
    }
}
