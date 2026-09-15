package com.gencnis.enson.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.ui.theme.EnSonTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyState_showsMessageAndCreateButton() {
        composeRule.setContent {
            EnSonTheme {
                HomeScreen(
                    tracks = emptyList(),
                    todayText = "15 Eylül 2026, Salı"
                )
            }
        }

        composeRule
            .onNodeWithText("Henüz takip yok")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("+ Yeni takip")
            .assertIsDisplayed()
    }

    @Test
    fun populatedState_showsTrackInformation() {
        composeRule.setContent {
            EnSonTheme {
                HomeScreen(
                    todayText = "15 Eylül 2026, Salı",
                    tracks = listOf(
                        TrackUiModel(
                            id = 1,
                            name = "Çiçek sulama",
                            category = TrackCategory.HOME,
                            tone = TrackTone.SAGE,
                            icon = TrackIcon.PLANT,
                            lastDateText = "14 Eylül 2026",
                            elapsedDays = 1,
                            scheduleText = "2 gün kaldı"
                        )
                    )
                )
            }
        }

        composeRule
            .onNodeWithText("Çiçek sulama")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Dün")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("2 gün kaldı")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Bugün yaptım")
            .assertIsDisplayed()
    }

    @Test
    fun personalFilter_hidesHomeTracks() {
        composeRule.setContent {
            EnSonTheme {
                HomeScreen(
                    todayText = "15 Eylül 2026, Salı",
                    tracks = listOf(
                        TrackUiModel(
                            id = 1,
                            name = "Regl başlangıcı",
                            category = TrackCategory.PERSONAL,
                            type = TrackType.PERIOD_START,
                            tone = TrackTone.PINK,
                            icon = TrackIcon.PERIOD,
                            lastDateText = "8 Eylül 2026",
                            elapsedDays = 7
                        ),
                        TrackUiModel(
                            id = 2,
                            name = "Çiçek sulama",
                            category = TrackCategory.HOME,
                            tone = TrackTone.SAGE,
                            icon = TrackIcon.PLANT,
                            lastDateText = "14 Eylül 2026",
                            elapsedDays = 1
                        )
                    )
                )
            }
        }

        composeRule
            .onNodeWithText("Kişisel")
            .performClick()

        composeRule
            .onNodeWithText("Regl başlangıcı")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Çiçek sulama")
            .assertDoesNotExist()
    }

    @Test
    fun quickLog_callsCallbackWithCorrectTrackId() {
        var clickedTrackId: Long? = null

        composeRule.setContent {
            EnSonTheme {
                HomeScreen(
                    tracks = listOf(
                        TrackUiModel(
                            id = 42,
                            name = "Çarşaf değişimi",
                            category = TrackCategory.HOME,
                            tone = TrackTone.YELLOW,
                            icon = TrackIcon.BED,
                            elapsedDays = 3
                        )
                    ),
                    onQuickLog = { id ->
                        clickedTrackId = id
                    }
                )
            }
        }

        composeRule
            .onNodeWithText("Bugün yaptım")
            .performClick()

        assertEquals(
            42L,
            clickedTrackId
        )
    }

    @Test
    fun periodTracking_usesStartButtonInsteadOfQuickLogButton() {
        composeRule.setContent {
            EnSonTheme {
                HomeScreen(
                    tracks = listOf(
                        TrackUiModel(
                            id = 1,
                            name = "Regl başlangıcı",
                            category = TrackCategory.PERSONAL,
                            type = TrackType.PERIOD_START,
                            tone = TrackTone.PINK,
                            icon = TrackIcon.PERIOD,
                            elapsedDays = 7
                        )
                    )
                )
            }
        }

        composeRule
            .onNodeWithText("Başlangıç ekle")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Bugün yaptım")
            .assertDoesNotExist()
    }
}