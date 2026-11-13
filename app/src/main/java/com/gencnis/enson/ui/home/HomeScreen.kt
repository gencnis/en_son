package com.gencnis.enson.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gencnis.enson.model.StarterSuggestion
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.model.starterSuggestions
import com.gencnis.enson.ui.common.color
import com.gencnis.enson.ui.common.currentEpochDay
import com.gencnis.enson.ui.common.formatEpochDayWithWeekday
import com.gencnis.enson.ui.common.vector
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.Fraunces
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.OverdueTerracotta
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.SoftBorder
import com.gencnis.enson.ui.theme.SurfacePaper
import com.gencnis.enson.ui.theme.WarmCream

data class TrackUiModel(
    val id: Long,
    val name: String,
    val category: TrackCategory,
    val type: TrackType = TrackType.NORMAL,
    val tone: TrackTone = TrackTone.PINK,
    val icon: TrackIcon = TrackIcon.OTHER,
    val lastDateText: String? = null,
    val elapsedDays: Int? = null,
    val scheduleText: String? = null,
    // Due today or late; shown with emphasis.
    val scheduleDue: Boolean = false
)

private enum class HomeFilter(
    val label: String
) {
    ALL("Tümü"),
    PERSONAL("Kişisel"),
    HOME("Ev"),
    OTHER("Diğer")
}

@Composable
fun HomeScreen(
    tracks: List<TrackUiModel> = emptyList(),
    todayText: String =
        formatEpochDayWithWeekday(currentEpochDay()),
    onCreateTrack: () -> Unit = {},
    onSuggestionClick: (StarterSuggestion) -> Unit = {},
    onTrackClick: (Long) -> Unit = {},
    onQuickLog: (Long) -> Unit = {}
) {
    var selectedFilter by rememberSaveable {
        mutableStateOf(HomeFilter.ALL)
    }

    val visibleTracks = tracks.filter { track ->
        when (selectedFilter) {
            HomeFilter.ALL -> true
            HomeFilter.PERSONAL ->
                track.category == TrackCategory.PERSONAL

            HomeFilter.HOME ->
                track.category == TrackCategory.HOME

            HomeFilter.OTHER ->
                track.category == TrackCategory.OTHER
        }
    }

    Scaffold(
        containerColor = WarmCream,
        bottomBar = {
            Surface(
                color = WarmCream
            ) {
                Button(
                    onClick = onCreateTrack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 10.dp,
                            bottom = 14.dp
                        )
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Plum
                    )
                ) {
                    Text(
                        text = "+ Yeni takip",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                HomeHeader(
                    todayText = todayText
                )
            }

            // Filters only make sense once there is something to filter.
            if (tracks.isNotEmpty()) {
                item {
                    FilterRow(
                        selected = selectedFilter,
                        onSelected = {
                            selectedFilter = it
                        }
                    )
                }
            }

            when {
                tracks.isEmpty() -> {
                    item {
                        EmptyState(
                            onSuggestionClick =
                                onSuggestionClick
                        )
                    }
                }

                visibleTracks.isEmpty() -> {
                    item {
                        FilterEmptyState()
                    }
                }

                else -> {
                    items(
                        items = visibleTracks,
                        key = { it.id }
                    ) { track ->
                        TrackCard(
                            track = track,
                            onClick = {
                                onTrackClick(track.id)
                            },
                            onQuickLog = {
                                onQuickLog(track.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    todayText: String
) {
    var showDataInfo by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "En Son",
                style = MaterialTheme.typography.displaySmall,
                color = DarkText,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = todayText,
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText
            )
        }

        IconButton(
            onClick = {
                showDataInfo = true
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Verilerin hakkında",
                tint = MutedText
            )
        }
    }

    if (showDataInfo) {
        DataInfoDialog(
            onDismiss = {
                showDataInfo = false
            }
        )
    }
}

@Composable
private fun DataInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePaper,
        title = {
            Text(
                text = "Veriler bu cihazda saklanır",
                color = DarkText
            )
        },
        text = {
            Text(
                text =
                    "En Son hesap, internet bağlantısı, reklam veya analitik kullanmaz. " +
                            "Takiplerin ve notların yalnızca bu cihazda tutulur; " +
                            "kimseyle paylaşılmaz ve buluta yedeklenmez.\n\n" +
                            "Uygulamayı kaldırırsan veya uygulama verilerini temizlersen " +
                            "tüm kayıtların kalıcı olarak silinir.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkText
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Tamam",
                    color = Plum,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun FilterRow(
    selected: HomeFilter,
    onSelected: (HomeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        horizontalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {
        HomeFilter.entries.forEach { filter ->
            val isSelected = selected == filter

            Surface(
                onClick = {
                    onSelected(filter)
                },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) {
                    Plum
                } else {
                    Color.Transparent
                },
                border = if (isSelected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = SoftBorder
                    )
                }
            ) {
                Text(
                    text = filter.label,
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) {
                        Color.White
                    } else {
                        MutedText
                    },
                    fontWeight = if (isSelected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    }
                )
            }
        }
    }
}

@Composable
private fun TrackCard(
    track: TrackUiModel,
    onClick: () -> Unit,
    onQuickLog: () -> Unit
) {
    val accentColor = track.tone.color()

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = accentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 17.dp
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = SurfacePaper
                ) {
                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Icon(
                            imageVector =
                                track.icon.vector(),
                            contentDescription = null,
                            tint = Plum,
                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(13.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.name,
                        style =
                            MaterialTheme.typography.titleMedium,
                        color = DarkText,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = track.lastDateText?.let {
                            "Son kayıt  ·  $it"
                        } ?: track.category.label,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // Wraps the button below the day count when the text is
            // large (small screens, big font settings) instead of
            // squeezing either of them.
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalArrangement =
                    Arrangement.spacedBy(12.dp),
                itemVerticalAlignment = Alignment.Bottom
            ) {

                Column(
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    ElapsedTime(
                        days = track.elapsedDays
                    )

                    track.scheduleText?.let {
                        Spacer(
                            modifier =
                                Modifier.height(9.dp)
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(12.dp),
                            color =
                                SurfacePaper.copy(
                                    alpha = 0.72f
                                )
                        ) {
                            Text(
                                text = it,
                                modifier =
                                    Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium,
                                color = if (track.scheduleDue) {
                                    OverdueTerracotta
                                } else {
                                    DarkText
                                },
                                fontWeight =
                                    if (track.scheduleDue) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Medium
                                    }
                            )
                        }
                    }
                }

                QuickLogButton(
                    loggedToday = track.elapsedDays == 0,
                    isPeriod =
                        track.type == TrackType.PERIOD_START,
                    onClick = onQuickLog
                )
            }
        }
    }
}

@Composable
private fun QuickLogButton(
    loggedToday: Boolean,
    isPeriod: Boolean,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    // Fills in once today's entry exists, so the card itself
    // confirms the tap; tapping again offers to edit it.
    val containerColor by animateColorAsState(
        targetValue = if (loggedToday) {
            Plum
        } else {
            SurfacePaper
        },
        label = "quickLogContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (loggedToday) {
            Color.White
        } else {
            Plum
        },
        label = "quickLogContent"
    )

    Surface(
        onClick = {
            if (!loggedToday) {
                haptics.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )
            }
            onClick()
        },
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = if (loggedToday) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = Plum.copy(alpha = 0.18f)
            )
        }
    ) {
        Row(
            modifier = Modifier
                .animateContentSize()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            if (loggedToday) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )
            }

            Text(
                text = when {
                    loggedToday && isPeriod ->
                        "Eklendi"

                    loggedToday ->
                        "Yapıldı"

                    isPeriod ->
                        "Başlangıç ekle"

                    else ->
                        "Bugün yaptım"
                },
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ElapsedTime(
    days: Int?
) {
    when (days) {
        null -> {
            Text(
                text = "Henüz kayıt yok",
                fontFamily = Fraunces,
                color = DarkText,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        0 -> {
            Text(
                text = "Bugün",
                fontFamily = Fraunces,
                color = DarkText,
                fontSize = 33.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        1 -> {
            Text(
                text = "Dün",
                fontFamily = Fraunces,
                color = DarkText,
                fontSize = 33.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        else -> {
            // One text so "gün geçti" wraps as words under the number.
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontFamily = Fraunces,
                            color = DarkText,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(days.toString())
                    }

                    withStyle(
                        SpanStyle(
                            color = MutedText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("  gün geçti")
                    }
                },
                lineHeight = 46.sp
            )
        }
    }
}

@Composable
private fun EmptyState(
    onSuggestionClick: (StarterSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = CardPink
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Plum,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Henüz takip yok",
            style =
                MaterialTheme.typography.headlineSmall,
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "En son ne zaman yaptığını unutmak istemediğin şeyleri burada tutabilirsin.",
            style =
                MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MutedText
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Text(
            text = "Başlamak için birkaç fikir",
            modifier = Modifier.fillMaxWidth(),
            style =
                MaterialTheme.typography.titleMedium,
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Column(
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            starterSuggestions.forEach { suggestion ->
                Surface(
                    onClick = {
                        onSuggestionClick(suggestion)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SurfacePaper,
                    border = BorderStroke(
                        width = 1.dp,
                        color = SoftBorder.copy(
                            alpha = 0.75f
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = suggestion.tone.color()
                        ) {
                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Icon(
                                    imageVector =
                                        suggestion.icon.vector(),
                                    contentDescription = null,
                                    tint = Plum,
                                    modifier =
                                        Modifier.size(19.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Text(
                            text = suggestion.name,
                            modifier = Modifier.weight(1f),
                            style =
                                MaterialTheme.typography.bodyLarge,
                            color = DarkText,
                            fontWeight = FontWeight.Medium
                        )

                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = Plum.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text =
                "Veriler bu cihazda saklanır. Uygulamayı kaldırırsan kayıtların da silinir.",
            modifier = Modifier.fillMaxWidth(),
            style =
                MaterialTheme.typography.bodyMedium,
            color = MutedText
        )
    }
}

@Composable
private fun FilterEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 42.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bu kategoride takip yok",
            style =
                MaterialTheme.typography.titleLarge,
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Başka bir kategori seçebilir veya yeni bir takip oluşturabilirsin.",
            style =
                MaterialTheme.typography.bodyLarge,
            color = MutedText
        )
    }
}

@Preview(
    name = "Boş durum",
    showBackground = true,
    backgroundColor = 0xFFFBF7F2
)
@Composable
private fun EmptyHomePreview() {
    EnSonTheme {
        HomeScreen(
            todayText =
                "15 Eylül 2026, Salı"
        )
    }
}

@Preview(
    name = "Takipler",
    showBackground = true,
    backgroundColor = 0xFFFBF7F2
)
@Composable
private fun PopulatedHomePreview() {
    EnSonTheme {
        HomeScreen(
            todayText =
                "15 Eylül 2026, Salı",
            tracks = listOf(
                TrackUiModel(
                    id = 1,
                    name = "Regl başlangıcı",
                    category =
                        TrackCategory.PERSONAL,
                    type =
                        TrackType.PERIOD_START,
                    tone = TrackTone.PINK,
                    icon = TrackIcon.PERIOD,
                    lastDateText =
                        "8 Eylül 2026",
                    elapsedDays = 7
                ),
                TrackUiModel(
                    id = 2,
                    name = "Çiçek sulama",
                    category =
                        TrackCategory.HOME,
                    tone = TrackTone.SAGE,
                    icon = TrackIcon.PLANT,
                    lastDateText =
                        "14 Eylül 2026",
                    elapsedDays = 1,
                    scheduleText =
                        "2 gün kaldı"
                ),
                TrackUiModel(
                    id = 3,
                    name = "Çarşaf değişimi",
                    category =
                        TrackCategory.HOME,
                    tone = TrackTone.YELLOW,
                    icon = TrackIcon.BED,
                    lastDateText =
                        "15 Eylül 2026",
                    elapsedDays = 0,
                    scheduleText =
                        "6 gün kaldı"
                ),
                TrackUiModel(
                    id = 4,
                    name =
                        "Kahve makinesi temizliği",
                    category =
                        TrackCategory.HOME,
                    tone = TrackTone.PINK,
                    icon = TrackIcon.COFFEE,
                    lastDateText =
                        "6 Eylül 2026",
                    elapsedDays = 9,
                    scheduleText =
                        "Bugün zamanı"
                )
            )
        )
    }
}