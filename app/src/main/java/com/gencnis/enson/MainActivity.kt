package com.gencnis.enson

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.gencnis.enson.data.local.EnSonDatabase
import com.gencnis.enson.data.local.TrackEntity
import com.gencnis.enson.data.local.TrackRecordEntity
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.ui.home.HomeScreen
import com.gencnis.enson.ui.home.TrackUiModel
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.track.NewTrackScreen
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            EnSonTheme {

                val database = remember {
                    EnSonDatabase.getInstance(
                        applicationContext
                    )
                }

                val trackDao = remember(database) {
                    database.trackDao()
                }

                val tracksFlow = remember(trackDao) {
                    trackDao.observeTracks()
                }

                val recordsFlow = remember(trackDao) {
                    trackDao.observeAllRecords()
                }

                val tracks by tracksFlow.collectAsState(
                    initial = emptyList()
                )

                val records by recordsFlow.collectAsState(
                    initial = emptyList()
                )

                var showNewTrackScreen by rememberSaveable {
                    mutableStateOf(false)
                }

                val todayEpochDay = currentEpochDay()

                val homeTracks = tracks.map { track ->

                    val latestRecord = records
                        .asSequence()
                        .filter {
                            it.trackId == track.id
                        }
                        .maxWithOrNull(
                            compareBy<TrackRecordEntity> {
                                it.dateEpochDay
                            }.thenBy {
                                it.id
                            }
                        )

                    val elapsedDays =
                        latestRecord?.let {
                            (todayEpochDay - it.dateEpochDay)
                                .coerceAtLeast(0)
                                .toInt()
                        }

                    TrackUiModel(
                        id = track.id,
                        name = track.name,
                        category = track.category,
                        type = track.type,
                        tone = track.tone,
                        icon = track.icon,
                        lastDateText =
                            latestRecord?.let {
                                formatEpochDay(
                                    it.dateEpochDay
                                )
                            },
                        elapsedDays = elapsedDays,
                        scheduleText =
                            scheduleText(
                                type = track.type,
                                repeatIntervalDays =
                                    track.repeatIntervalDays,
                                elapsedDays = elapsedDays
                            )
                    )
                }

                if (showNewTrackScreen) {

                    NewTrackScreen(
                        onBack = {
                            showNewTrackScreen = false
                        },
                        onCreate = { draft ->

                            lifecycleScope.launch {

                                val trackId =
                                    trackDao.insertTrack(
                                        TrackEntity(
                                            name = draft.name,
                                            category =
                                                draft.category,
                                            type = draft.type,
                                            icon = draft.icon,
                                            tone = draft.tone,
                                            repeatIntervalDays =
                                                draft
                                                    .repeatIntervalDays
                                        )
                                    )

                                val initialDate =
                                    draft.lastDateText
                                        ?.let {
                                            parseDateToEpochDay(
                                                it
                                            )
                                        }

                                if (
                                    initialDate != null &&
                                    initialDate <= currentEpochDay()
                                ) {
                                    trackDao.insertRecord(
                                        TrackRecordEntity(
                                            trackId = trackId,
                                            dateEpochDay =
                                                initialDate
                                        )
                                    )
                                }

                                showNewTrackScreen = false
                            }
                        }
                    )

                } else {

                    HomeScreen(
                        tracks = homeTracks,
                        onCreateTrack = {
                            showNewTrackScreen = true
                        },
                        onQuickLog = { trackId ->

                            lifecycleScope.launch {

                                val today =
                                    currentEpochDay()

                                val alreadyExists =
                                    trackDao
                                        .recordExistsOnDate(
                                            trackId =
                                                trackId,
                                            dateEpochDay =
                                                today
                                        )

                                if (!alreadyExists) {
                                    trackDao.insertRecord(
                                        TrackRecordEntity(
                                            trackId =
                                                trackId,
                                            dateEpochDay =
                                                today
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun currentEpochDay(): Long {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    )

    formatter.timeZone =
        TimeZone.getDefault()

    val todayText =
        formatter.format(Date())

    val utcFormatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    )

    utcFormatter.timeZone =
        TimeZone.getTimeZone("UTC")

    val date = utcFormatter.parse(
        todayText
    ) ?: return 0L

    return date.time / MILLIS_PER_DAY
}

private fun parseDateToEpochDay(
    text: String
): Long? {
    val formatter = SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.forLanguageTag("tr-TR")
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

private fun formatEpochDay(
    epochDay: Long
): String {
    val formatter = SimpleDateFormat(
        "d MMMM yyyy",
        Locale.forLanguageTag("tr-TR")
    )

    formatter.timeZone =
        TimeZone.getTimeZone("UTC")

    return formatter.format(
        Date(
            epochDay * MILLIS_PER_DAY
        )
    )
}

private fun scheduleText(
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
        repeatIntervalDays - elapsedDays

    return when {
        remaining > 0 ->
            "$remaining gün kaldı"

        remaining == 0 ->
            "Bugün zamanı"

        else ->
            "${-remaining} gün gecikti"
    }
}

private const val MILLIS_PER_DAY =
    86_400_000L