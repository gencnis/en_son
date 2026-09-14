package com.gencnis.enson.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.CardSage
import com.gencnis.enson.ui.theme.CardYellow
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.WarmCream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import com.gencnis.enson.ui.theme.Fraunces
import com.gencnis.enson.ui.theme.SoftBorder
import com.gencnis.enson.ui.theme.SurfacePaper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.outlined.Schedule


enum class TrackCategory(val label: String) {
    PERSONAL("Kişisel"),
    HOME("Ev"),
    OTHER("Diğer")
}

enum class TrackType {
    NORMAL,
    PERIOD_START
}

enum class TrackTone {
    PINK,
    SAGE,
    YELLOW
}

enum class TrackIcon {
    PERIOD,
    PLANT,
    BED,
    COFFEE,
    FILTER,
    OTHER
}

data class TrackUiModel(
    val id: Long,
    val name: String,
    val category: TrackCategory,
    val type: TrackType = TrackType.NORMAL,
    val tone: TrackTone = TrackTone.PINK,
    val icon: TrackIcon = TrackIcon.OTHER,
    val lastDateText: String? = null,
    val elapsedDays: Int? = null,
    val scheduleText: String? = null
)

private enum class HomeFilter(val label: String) {
    ALL("Tümü"),
    PERSONAL("Kişisel"),
    HOME("Ev"),
    OTHER("Diğer")
}

@Composable
fun HomeScreen(
    tracks: List<TrackUiModel> = emptyList(),
    todayText: String = currentTurkishDate(),
    onCreateTrack: () -> Unit = {},
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
            Surface(color = WarmCream) {
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
                        fontSize = 16.sp,
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
                Column {
                    Text(
                        text = "En Son",
                        style = MaterialTheme.typography.displaySmall,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = todayText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MutedText
                    )
                }
            }

            item {
                FilterRow(
                    selected = selectedFilter,
                    onSelected = {
                        selectedFilter = it
                    }
                )
            }

            if (tracks.isEmpty()) {

                item {
                    EmptyState()
                }

            } else if (visibleTracks.isEmpty()) {

                item {
                    FilterEmptyState()
                }

            } else {

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

@Composable
private fun FilterRow(
    selected: HomeFilter,
    onSelected: (HomeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HomeFilter.entries.forEach { filter ->

            val isSelected = selected == filter

            Surface(
                onClick = { onSelected(filter) },
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
                        1.dp,
                        SoftBorder
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
    val accentColor = when (track.tone) {
        TrackTone.PINK -> CardPink
        TrackTone.SAGE -> CardSage
        TrackTone.YELLOW -> CardYellow
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfacePaper
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SoftBorder.copy(alpha = 0.72f)
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
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = accentColor
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = track.iconVector(),
                            contentDescription = null,
                            tint = Plum,
                            modifier = Modifier.size(20.dp)
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
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = track.lastDateText?.let {
                            "Son kayıt  ·  $it"
                        } ?: "Henüz kayıt yok",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    ElapsedTime(
                        days = track.elapsedDays
                    )

                    if (track.scheduleText != null) {

                        Spacer(
                            modifier = Modifier.height(9.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = accentColor.copy(alpha = 0.78f)
                        ) {
                            Text(
                                text = track.scheduleText,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 6.dp
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = DarkText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Surface(
                    onClick = onQuickLog,
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        Plum.copy(alpha = 0.26f)
                    )
                ) {
                    Text(
                        text = if (
                            track.type == TrackType.PERIOD_START
                        ) {
                            "Başlangıç ekle"
                        } else {
                            "Bugün yaptım"
                        },
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        ),
                        color = Plum,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
                fontSize = 25.sp,
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
            Row(
                verticalAlignment = Alignment.Bottom
            ) {

                Text(
                    text = days.toString(),
                    fontFamily = Fraunces,
                    color = DarkText,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 46.sp
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = "gün geçti",
                    modifier = Modifier.padding(
                        bottom = 6.dp
                    ),
                    color = MutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun elapsedText(days: Int?): String {
    return when (days) {
        null -> "Henüz kayıt yok"
        0 -> "Bugün"
        1 -> "Dün"
        else -> "$days gün geçti"
    }
}

@Composable
private fun EmptyState() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Plum,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Henüz takip yok",
            style = MaterialTheme.typography.headlineSmall,
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                "En son ne zaman yaptığını unutmak istemediğin şeyleri burada tutabilirsin.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Başlamak için birkaç fikir",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        listOf(
            "Regl başlangıcı",
            "Çiçek sulama",
            "Çarşaf değişimi",
            "Kahve makinesi temizliği",
            "Filtre değişimi"
        ).forEach { suggestion ->

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.60f)
            ) {

                Text(
                    text = suggestion,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkText
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Veriler bu cihazda saklanır.",
            style = MaterialTheme.typography.bodyMedium,
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Bu kategoride takip yok",
            style = MaterialTheme.typography.titleLarge,
            color = DarkText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Başka bir kategori seçebilir veya yeni bir takip oluşturabilirsin.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText
        )
    }
}

private fun currentTurkishDate(): String {
    return SimpleDateFormat(
        "d MMMM yyyy, EEEE",
        Locale("tr", "TR")
    ).format(Date())
}

@Preview(
    name = "Boş durum",
    showBackground = true,
    backgroundColor = 0xFFFFF9F3
)
@Composable
private fun EmptyHomePreview() {
    EnSonTheme {
        HomeScreen(
            todayText = "14 Eylül 2026, Pazartesi"
        )
    }
}

@Preview(
    name = "Takipler",
    showBackground = true,
    backgroundColor = 0xFFFFF9F3
)
@Composable
private fun PopulatedHomePreview() {

    EnSonTheme {

        HomeScreen(
            todayText = "14 Eylül 2026, Pazartesi",
            tracks = listOf(

                TrackUiModel(
                    id = 1,
                    name = "Regl başlangıcı",
                    category = TrackCategory.PERSONAL,
                    type = TrackType.PERIOD_START,
                    tone = TrackTone.PINK,
                    icon = TrackIcon.PERIOD,
                    lastDateText = "8 Eylül 2026",
                    elapsedDays = 6
                ),

                TrackUiModel(
                    id = 2,
                    name = "Çiçek sulama",
                    category = TrackCategory.HOME,
                    tone = TrackTone.SAGE,
                    icon = TrackIcon.PLANT,
                    lastDateText = "13 Eylül 2026",
                    elapsedDays = 1,
                    scheduleText = "2 gün kaldı"
                ),

                TrackUiModel(
                    id = 3,
                    name = "Çarşaf değişimi",
                    category = TrackCategory.HOME,
                    tone = TrackTone.YELLOW,
                    icon = TrackIcon.BED,
                    lastDateText = "14 Eylül 2026",
                    elapsedDays = 0,
                    scheduleText = "6 gün kaldı"
                ),

                TrackUiModel(
                    id = 4,
                    name = "Kahve makinesi temizliği",
                    category = TrackCategory.HOME,
                    tone = TrackTone.PINK,
                    icon = TrackIcon.COFFEE,
                    lastDateText = "6 Eylül 2026",
                    elapsedDays = 8,
                    scheduleText = "Bugün zamanı"
                )
            )
        )
    }
}

private fun TrackUiModel.iconVector(): ImageVector {
    return when (icon) {
        TrackIcon.PERIOD -> Icons.Outlined.WaterDrop
        TrackIcon.PLANT -> Icons.Outlined.LocalFlorist
        TrackIcon.BED -> Icons.Outlined.Bed
        TrackIcon.COFFEE -> Icons.Outlined.Coffee
        TrackIcon.FILTER -> Icons.Outlined.FilterAlt
        TrackIcon.OTHER -> Icons.Outlined.MoreHoriz
    }
}