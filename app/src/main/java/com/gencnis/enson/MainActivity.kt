package com.gencnis.enson

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gencnis.enson.data.local.EnSonDatabase
import com.gencnis.enson.data.local.TrackEntity
import com.gencnis.enson.data.local.TrackRecordEntity
import com.gencnis.enson.model.starterSuggestions
import com.gencnis.enson.ui.common.currentEpochDay
import com.gencnis.enson.ui.common.formatEpochDay
import com.gencnis.enson.ui.common.formatEpochDayWithWeekday
import com.gencnis.enson.ui.common.millisUntilNextLocalDay
import com.gencnis.enson.ui.common.scheduleStatus
import com.gencnis.enson.ui.home.HomeScreen
import com.gencnis.enson.ui.home.TrackUiModel
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.WarmCream
import com.gencnis.enson.ui.track.NewTrackDraft
import com.gencnis.enson.ui.track.NewTrackScreen
import com.gencnis.enson.ui.track.TrackDetailScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Day counts follow the device's calendar date: refreshed when the
    // app comes back to the foreground and at local midnight.
    private var todayEpochDay by mutableLongStateOf(
        currentEpochDay()
    )

    override fun onResume() {
        super.onResume()

        todayEpochDay = currentEpochDay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The app is always light, so keep dark system bar icons
        // even when the phone is in dark mode.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

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

                // Null until Room's first emission, so we neither flash
                // the empty state nor drop a restored detail screen.
                val tracks by tracksFlow.collectAsState(
                    initial = null
                )

                val records by recordsFlow.collectAsState(
                    initial = emptyList()
                )

                val snackbarHostState = remember {
                    SnackbarHostState()
                }

                val coroutineScope =
                    rememberCoroutineScope()

                var showNewTrackScreen by rememberSaveable {
                    mutableStateOf(false)
                }

                var openTrackId by rememberSaveable {
                    mutableStateOf<Long?>(null)
                }

                var editingOpenTrack by rememberSaveable {
                    mutableStateOf(false)
                }

                // Set when the new track form is opened from a suggestion.
                var newTrackSuggestionName by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(millisUntilNextLocalDay())
                        todayEpochDay = currentEpochDay()
                    }
                }

                val openTrack =
                    openTrackId?.let { id ->
                        tracks?.firstOrNull {
                            it.id == id
                        }
                    }

                LaunchedEffect(tracks, openTrackId) {
                    if (
                        tracks != null &&
                        openTrackId != null &&
                        openTrack == null
                    ) {
                        openTrackId = null
                        editingOpenTrack = false
                    }
                }

                BackHandler(
                    enabled =
                        showNewTrackScreen ||
                                openTrackId != null
                ) {
                    when {
                        showNewTrackScreen ->
                            showNewTrackScreen = false

                        editingOpenTrack ->
                            editingOpenTrack = false

                        else ->
                            openTrackId = null
                    }
                }

                suspend fun writeRecord(
                    block: suspend () -> Unit
                ) {
                    try {
                        block()
                    } catch (_: SQLiteConstraintException) {
                        snackbarHostState.showSnackbar(
                            message =
                                "Bu tarihte zaten bir kayıt var",
                            duration =
                                SnackbarDuration.Short
                        )
                    }
                }

                val screen = when {
                    tracks == null -> Screen.LOADING
                    showNewTrackScreen -> Screen.NEW_TRACK
                    openTrack != null &&
                            editingOpenTrack -> Screen.EDIT_TRACK
                    openTrack != null -> Screen.DETAIL
                    else -> Screen.HOME
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WarmCream)
                ) {

                    AnimatedContent(
                        targetState = screen,
                        transitionSpec = {
                            screenTransition(
                                forward =
                                    targetState.depth >=
                                            initialState.depth
                            )
                        },
                        label = "screen"
                    ) { target ->

                        val loadedTracks = tracks.orEmpty()

                        when (target) {
                            Screen.LOADING -> Unit

                            Screen.NEW_TRACK -> {

                                NewTrackScreen(
                                    suggestion =
                                        starterSuggestions.firstOrNull {
                                            it.name ==
                                                    newTrackSuggestionName
                                        },
                                    todayEpochDay = todayEpochDay,
                                    existingNames = loadedTracks
                                        .map { it.name }
                                        .toSet(),
                                    onBack = {
                                        showNewTrackScreen = false
                                    },
                                    onSubmit = { draft ->

                                        coroutineScope.launch {

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
                                                draft.lastDateEpochDay

                                            if (
                                                initialDate != null &&
                                                initialDate <=
                                                currentEpochDay()
                                            ) {
                                                trackDao.insertRecord(
                                                    TrackRecordEntity(
                                                        trackId =
                                                            trackId,
                                                        dateEpochDay =
                                                            initialDate
                                                    )
                                                )
                                            }

                                            showNewTrackScreen =
                                                false
                                        }
                                    }
                                )
                            }

                            Screen.EDIT_TRACK -> {

                                // Gone mid-transition, e.g. just deleted.
                                if (openTrack == null) {
                                    return@AnimatedContent
                                }

                                NewTrackScreen(
                                    initial = openTrack.toDraft(),
                                    todayEpochDay = todayEpochDay,
                                    existingNames = loadedTracks
                                        .map { it.name }
                                        .toSet(),
                                    onBack = {
                                        editingOpenTrack = false
                                    },
                                    onSubmit = { draft ->

                                        coroutineScope.launch {

                                            trackDao.updateTrack(
                                                openTrack.copy(
                                                    name = draft.name,
                                                    category =
                                                        draft.category,
                                                    icon = draft.icon,
                                                    tone = draft.tone,
                                                    repeatIntervalDays =
                                                        draft
                                                            .repeatIntervalDays
                                                )
                                            )

                                            editingOpenTrack =
                                                false
                                        }
                                    }
                                )
                            }

                            Screen.DETAIL -> {

                                if (openTrack == null) {
                                    return@AnimatedContent
                                }

                                TrackDetailScreen(
                                    track = openTrack,
                                    records = records.filter {
                                        it.trackId == openTrack.id
                                    },
                                    todayEpochDay =
                                        todayEpochDay,
                                    onBack = {
                                        openTrackId = null
                                    },
                                    onEditTrack = {
                                        editingOpenTrack = true
                                    },
                                    onDeleteTrack = {
                                        openTrackId = null

                                        coroutineScope.launch {
                                            // Records go with it via
                                            // ON DELETE CASCADE.
                                            trackDao.deleteTrack(
                                                openTrack
                                            )
                                        }
                                    },
                                    onAddRecord = { date, note ->

                                        coroutineScope.launch {
                                            writeRecord {
                                                trackDao.insertRecord(
                                                    TrackRecordEntity(
                                                        trackId =
                                                            openTrack.id,
                                                        dateEpochDay =
                                                            date,
                                                        note = note
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onUpdateRecord = { record ->

                                        coroutineScope.launch {
                                            writeRecord {
                                                trackDao.updateRecord(
                                                    record
                                                )
                                            }
                                        }
                                    },
                                    onDeleteRecord = { record ->

                                        coroutineScope.launch {

                                            trackDao.deleteRecord(
                                                record
                                            )

                                            val result =
                                                snackbarHostState
                                                    .showSnackbar(
                                                        message =
                                                            "Kayıt silindi",
                                                        actionLabel =
                                                            "Geri al",
                                                        withDismissAction =
                                                            true,
                                                        duration =
                                                            SnackbarDuration.Long
                                                    )

                                            if (
                                                result ==
                                                SnackbarResult.ActionPerformed
                                            ) {
                                                writeRecord {
                                                    trackDao.insertRecord(
                                                        record
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            Screen.HOME -> {

                                HomeScreen(
                                    todayText =
                                        formatEpochDayWithWeekday(
                                            todayEpochDay
                                        ),
                                    tracks = loadedTracks.map { track ->
                                        track.toUiModel(
                                            records = records,
                                            todayEpochDay =
                                                todayEpochDay
                                        )
                                    },
                                    onCreateTrack = {
                                        newTrackSuggestionName = null
                                        showNewTrackScreen =
                                            true
                                    },
                                    onSuggestionClick = {
                                        newTrackSuggestionName = it.name
                                        showNewTrackScreen = true
                                    },
                                    onTrackClick = { trackId ->
                                        openTrackId = trackId
                                    },
                                    onQuickLog = { trackId ->

                                        coroutineScope.launch {

                                            val today =
                                                currentEpochDay()

                                            todayEpochDay = today

                                            val alreadyExists =
                                                trackDao
                                                    .recordExistsOnDate(
                                                        trackId =
                                                            trackId,
                                                        dateEpochDay =
                                                            today
                                                    )

                                            if (alreadyExists) {

                                                val result =
                                                    snackbarHostState
                                                        .showSnackbar(
                                                            message =
                                                                "Bugün için zaten bir kayıt var",
                                                            actionLabel =
                                                                "Düzenle",
                                                            duration =
                                                                SnackbarDuration.Short
                                                        )

                                                if (
                                                    result ==
                                                    SnackbarResult.ActionPerformed
                                                ) {
                                                    openTrackId =
                                                        trackId
                                                }

                                            } else {

                                                val recordId =
                                                    trackDao.insertRecord(
                                                        TrackRecordEntity(
                                                            trackId =
                                                                trackId,
                                                            dateEpochDay =
                                                                today
                                                        )
                                                    )

                                                val result =
                                                    snackbarHostState
                                                        .showSnackbar(
                                                            message =
                                                                "Kaydedildi",
                                                            actionLabel =
                                                                "Geri al",
                                                            withDismissAction =
                                                                true,
                                                            duration =
                                                                SnackbarDuration.Long
                                                        )

                                                if (
                                                    result ==
                                                    SnackbarResult.ActionPerformed
                                                ) {
                                                    trackDao
                                                        .deleteRecordById(
                                                            recordId
                                                        )
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if (
                        !showNewTrackScreen &&
                        !editingOpenTrack
                    ) {
                        SnackbarHost(
                            hostState =
                                snackbarHostState,
                            modifier = Modifier
                                .align(
                                    Alignment.BottomCenter
                                )
                                .navigationBarsPadding()
                                .padding(
                                    start = 24.dp,
                                    end = 24.dp,
                                    bottom = 82.dp
                                )
                        )
                    }
                }
            }
        }
    }
}

// Depth orders screens so moving deeper slides forward
// and going back slides the other way.
private enum class Screen(
    val depth: Int
) {
    LOADING(0),
    HOME(0),
    DETAIL(1),
    NEW_TRACK(1),
    EDIT_TRACK(2)
}

private fun screenTransition(
    forward: Boolean
): ContentTransform {
    val direction = if (forward) 1 else -1

    return (
            fadeIn(tween(220, delayMillis = 60)) +
                    slideInHorizontally(tween(280)) {
                        direction * it / 10
                    }
            ) togetherWith (
            fadeOut(tween(120)) +
                    slideOutHorizontally(tween(280)) {
                        -direction * it / 10
                    }
            )
}

private fun TrackEntity.toDraft(): NewTrackDraft {
    return NewTrackDraft(
        name = name,
        category = category,
        type = type,
        icon = icon,
        tone = tone,
        lastDateEpochDay = null,
        repeatIntervalDays = repeatIntervalDays
    )
}

private fun TrackEntity.toUiModel(
    records: List<TrackRecordEntity>,
    todayEpochDay: Long
): TrackUiModel {

    val latestRecord = records
        .asSequence()
        .filter {
            it.trackId == id
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
            (
                    todayEpochDay -
                            it.dateEpochDay
                    )
                .coerceAtLeast(0)
                .toInt()
        }

    val schedule =
        scheduleStatus(
            type = type,
            repeatIntervalDays =
                repeatIntervalDays,
            elapsedDays =
                elapsedDays
        )

    return TrackUiModel(
        id = id,
        name = name,
        category = category,
        type = type,
        tone = tone,
        icon = icon,
        lastDateText =
            latestRecord?.let {
                formatEpochDay(
                    it.dateEpochDay
                )
            },
        elapsedDays = elapsedDays,
        scheduleText = schedule?.text,
        scheduleDue = schedule?.isDue == true
    )
}
