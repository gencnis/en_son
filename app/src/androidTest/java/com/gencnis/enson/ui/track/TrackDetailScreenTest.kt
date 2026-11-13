package com.gencnis.enson.ui.track

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.gencnis.enson.data.local.TrackEntity
import com.gencnis.enson.data.local.TrackRecordEntity
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.ui.common.formatEpochDay
import com.gencnis.enson.ui.theme.EnSonTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TrackDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val today = 20_000L

    private val track = TrackEntity(
        id = 7,
        name = "Çiçek sulama",
        category = TrackCategory.HOME,
        type = TrackType.NORMAL,
        icon = TrackIcon.PLANT,
        tone = TrackTone.SAGE,
        repeatIntervalDays = 5
    )

    @Test
    fun summary_usesNewestRecord() {
        composeRule.setContent {
            EnSonTheme {
                TrackDetailScreen(
                    track = track,
                    records = listOf(
                        TrackRecordEntity(
                            id = 1,
                            trackId = 7,
                            dateEpochDay = today - 3
                        ),
                        TrackRecordEntity(
                            id = 2,
                            trackId = 7,
                            dateEpochDay = today - 10
                        )
                    ),
                    todayEpochDay = today
                )
            }
        }

        composeRule
            .onNodeWithText("3")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("2 gün kaldı")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("2 kayıt")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(
                "Son kayıt  ·  ${formatEpochDay(today - 3)}"
            )
            .assertIsDisplayed()
    }

    @Test
    fun addRecord_onTakenDate_blocksSaveAndOffersEdit() {
        composeRule.setContent {
            EnSonTheme {
                TrackDetailScreen(
                    track = track,
                    records = listOf(
                        TrackRecordEntity(
                            id = 1,
                            trackId = 7,
                            dateEpochDay = today
                        )
                    ),
                    todayEpochDay = today
                )
            }
        }

        composeRule
            .onNodeWithText("Yeni kayıt ekle")
            .performClick()

        composeRule
            .onNodeWithText("Bu tarihte zaten bir kayıt var.")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Mevcut kaydı düzenle")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Kaydet")
            .assertIsNotEnabled()
    }

    @Test
    fun addRecord_savesTodayWithTrimmedNote() {
        var saved: Pair<Long, String?>? = null

        composeRule.setContent {
            EnSonTheme {
                TrackDetailScreen(
                    track = track,
                    records = emptyList(),
                    todayEpochDay = today,
                    onAddRecord = { date, note ->
                        saved = date to note
                    }
                )
            }
        }

        composeRule
            .onNodeWithText("Yeni kayıt ekle")
            .performClick()

        composeRule
            .onNode(hasSetTextAction())
            .performTextInput("  Salondakiler  ")

        composeRule
            .onNodeWithText("Kaydet")
            .performClick()

        assertEquals(
            today to "Salondakiler",
            saved
        )
    }

    @Test
    fun deleteTrack_requiresConfirmation() {
        var deleted = false

        composeRule.setContent {
            EnSonTheme {
                TrackDetailScreen(
                    track = track,
                    records = emptyList(),
                    todayEpochDay = today,
                    onDeleteTrack = {
                        deleted = true
                    }
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("Takibi sil")
            .performClick()

        assertFalse(deleted)

        composeRule
            .onNodeWithText("Takip silinsin mi?")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Sil")
            .performClick()

        assertTrue(deleted)
    }

    @Test
    fun periodTrack_neverShowsSchedule() {
        composeRule.setContent {
            EnSonTheme {
                TrackDetailScreen(
                    track = track.copy(
                        type = TrackType.PERIOD_START,
                        repeatIntervalDays = 2
                    ),
                    records = listOf(
                        TrackRecordEntity(
                            id = 1,
                            trackId = 7,
                            dateEpochDay = today - 9
                        )
                    ),
                    todayEpochDay = today
                )
            }
        }

        composeRule
            .onNodeWithText("9")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("7 gün gecikti")
            .assertDoesNotExist()
    }
}
