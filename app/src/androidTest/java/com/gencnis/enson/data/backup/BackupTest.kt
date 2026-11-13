package com.gencnis.enson.data.backup

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gencnis.enson.data.local.EnSonDatabase
import com.gencnis.enson.data.local.TrackEntity
import com.gencnis.enson.data.local.TrackRecordEntity
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupTest {

    private lateinit var source: EnSonDatabase
    private lateinit var target: EnSonDatabase

    private fun inMemoryDatabase(): EnSonDatabase {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext,
            EnSonDatabase::class.java
        ).build()
    }

    @Before
    fun setUp() {
        source = inMemoryDatabase()
        target = inMemoryDatabase()
    }

    @After
    fun tearDown() {
        source.close()
        target.close()
    }

    @Test
    fun exportThenRestore_reproducesEverything() = runBlocking {
        val dao = source.trackDao()

        val plants = dao.insertTrack(
            TrackEntity(
                name = "Çiçek sulama",
                category = TrackCategory.HOME,
                type = TrackType.NORMAL,
                icon = TrackIcon.PLANT,
                tone = TrackTone.SAGE,
                repeatIntervalDays = 3
            )
        )

        val period = dao.insertTrack(
            TrackEntity(
                name = "Regl başlangıcı",
                category = TrackCategory.PERSONAL,
                type = TrackType.PERIOD_START,
                icon = TrackIcon.PERIOD,
                tone = TrackTone.PINK,
                isArchived = true
            )
        )

        dao.insertRecord(
            TrackRecordEntity(
                trackId = plants,
                dateEpochDay = 20_000,
                note = "Salondakiler"
            )
        )

        dao.insertRecord(
            TrackRecordEntity(
                trackId = period,
                dateEpochDay = 19_990
            )
        )

        // Something already on the target device gets replaced.
        target.trackDao().insertTrack(
            TrackEntity(
                name = "Eski",
                category = TrackCategory.OTHER,
                type = TrackType.NORMAL,
                icon = TrackIcon.OTHER,
                tone = TrackTone.PEACH
            )
        )

        val json = source.readBackup().toJson()

        target.restoreBackup(parseBackup(json))

        assertEquals(
            source.trackDao().getAllTracks().sortedBy { it.id },
            target.trackDao().getAllTracks().sortedBy { it.id }
        )

        assertEquals(
            source.trackDao().getAllRecords().sortedBy { it.id },
            target.trackDao().getAllRecords().sortedBy { it.id }
        )
    }

    @Test(expected = BackupFormatException::class)
    fun parse_rejectsOtherJson() {
        parseBackup("""{"tracks": [], "records": []}""")
    }

    @Test(expected = BackupFormatException::class)
    fun parse_rejectsGarbage() {
        parseBackup("bu bir yedek değil")
    }

    @Test
    fun parse_toleratesUnknownValuesAndDropsOrphans() {
        val data = parseBackup(
            """
            {
              "app": "com.gencnis.enson",
              "format": 1,
              "tracks": [
                {
                  "id": 5,
                  "name": "  Diş kontrolü  ",
                  "category": "SOMETHING_NEW",
                  "type": "PERIOD_START",
                  "icon": "ROCKET",
                  "tone": "NEON",
                  "repeatIntervalDays": 28
                }
              ],
              "records": [
                { "id": 1, "trackId": 5, "dateEpochDay": 20000 },
                { "id": 2, "trackId": 5, "dateEpochDay": 20000 },
                { "id": 3, "trackId": 99, "dateEpochDay": 20001 }
              ]
            }
            """
        )

        val track = data.tracks.single()

        assertEquals("Diş kontrolü", track.name)
        assertEquals(TrackCategory.OTHER, track.category)
        assertEquals(TrackIcon.OTHER, track.icon)
        assertEquals(TrackTone.PINK, track.tone)
        // Periods never carry an interval.
        assertNull(track.repeatIntervalDays)

        // Same-day duplicate and the orphan record are dropped.
        assertEquals(listOf(1L), data.records.map { it.id })
        assertTrue(data.records.all { it.trackId == 5L })
    }
}
