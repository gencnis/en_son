package com.gencnis.enson.ui.track

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.CardSage
import com.gencnis.enson.ui.theme.CardYellow
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.SoftBorder
import com.gencnis.enson.ui.theme.SurfacePaper
import com.gencnis.enson.ui.theme.WarmCream

data class NewTrackDraft(
    val name: String,
    val category: TrackCategory,
    val type: TrackType,
    val icon: TrackIcon,
    val tone: TrackTone,
    val lastDateText: String?,
    val repeatIntervalDays: Int?
)

private data class StarterSuggestion(
    val name: String,
    val category: TrackCategory,
    val type: TrackType = TrackType.NORMAL,
    val icon: TrackIcon,
    val tone: TrackTone
)

private val starterSuggestions = listOf(
    StarterSuggestion(
        name = "Regl başlangıcı",
        category = TrackCategory.PERSONAL,
        type = TrackType.PERIOD_START,
        icon = TrackIcon.PERIOD,
        tone = TrackTone.PINK
    ),
    StarterSuggestion(
        name = "Çiçek sulama",
        category = TrackCategory.HOME,
        icon = TrackIcon.PLANT,
        tone = TrackTone.SAGE
    ),
    StarterSuggestion(
        name = "Çarşaf değişimi",
        category = TrackCategory.HOME,
        icon = TrackIcon.BED,
        tone = TrackTone.YELLOW
    ),
    StarterSuggestion(
        name = "Kahve makinesi temizliği",
        category = TrackCategory.HOME,
        icon = TrackIcon.COFFEE,
        tone = TrackTone.PINK
    ),
    StarterSuggestion(
        name = "Filtre değişimi",
        category = TrackCategory.HOME,
        icon = TrackIcon.FILTER,
        tone = TrackTone.SAGE
    )
)

@Composable
fun NewTrackScreen(
    onBack: () -> Unit = {},
    onCreate: (NewTrackDraft) -> Unit = {}
) {
    var name by rememberSaveable {
        mutableStateOf("")
    }

    var category by rememberSaveable {
        mutableStateOf(TrackCategory.PERSONAL)
    }

    var type by rememberSaveable {
        mutableStateOf(TrackType.NORMAL)
    }

    var icon by rememberSaveable {
        mutableStateOf(TrackIcon.OTHER)
    }

    var tone by rememberSaveable {
        mutableStateOf(TrackTone.PINK)
    }

    var lastDateText by rememberSaveable {
        mutableStateOf("")
    }

    var repeatIntervalText by rememberSaveable {
        mutableStateOf("")
    }

    val isPeriodTrack =
        type == TrackType.PERIOD_START

    val canCreate =
        name.trim().isNotEmpty()

    Scaffold(
        containerColor = WarmCream,
        bottomBar = {
            Surface(
                color = WarmCream
            ) {
                Button(
                    onClick = {
                        if (!canCreate) {
                            return@Button
                        }

                        onCreate(
                            NewTrackDraft(
                                name = name.trim(),
                                category = category,
                                type = type,
                                icon = icon,
                                tone = tone,
                                lastDateText =
                                    lastDateText
                                        .trim()
                                        .takeIf {
                                            it.isNotEmpty()
                                        },
                                repeatIntervalDays =
                                    if (isPeriodTrack) {
                                        null
                                    } else {
                                        repeatIntervalText
                                            .toIntOrNull()
                                            ?.takeIf {
                                                it > 0
                                            }
                                    }
                            )
                        )
                    },
                    enabled = canCreate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 10.dp,
                            bottom = 14.dp
                        )
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Plum,
                        disabledContainerColor =
                            Plum.copy(alpha = 0.22f)
                    )
                ) {
                    Text(
                        text = "Takibi oluştur",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 28.dp
                )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored
                                .Outlined
                                .ArrowBack,
                        contentDescription = "Geri",
                        tint = DarkText
                    )
                }

                Text(
                    text = "Yeni takip",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    color = DarkText
                )
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Neyi takip etmek istiyorsun?",
                style =
                    MaterialTheme
                        .typography
                        .displaySmall,
                color = DarkText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Küçük şeyleri aklında tutmak zorunda değilsin.",
                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            SectionTitle(
                text = "Takip adı"
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Örn. Diş kontrolü"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = premiumTextFieldColors()
            )

            Spacer(
                modifier = Modifier.height(26.dp)
            )

            SectionTitle(
                text = "Hızlı başla"
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                starterSuggestions.forEach {
                        suggestion ->

                    SuggestionRow(
                        suggestion = suggestion,
                        onClick = {
                            name = suggestion.name
                            category =
                                suggestion.category
                            type = suggestion.type
                            icon = suggestion.icon
                            tone = suggestion.tone

                            if (
                                suggestion.type ==
                                TrackType.PERIOD_START
                            ) {
                                repeatIntervalText = ""
                            }
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            SectionTitle(
                text = "Kategori"
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                TrackCategory.entries.forEach {
                        item ->

                    SelectionPill(
                        text = item.label,
                        selected =
                            category == item,
                        onClick = {
                            category = item
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            SectionTitle(
                text = "Görünüm"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "İkon",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                TrackIcon.entries.forEach {
                        item ->

                    IconChoice(
                        icon = item,
                        selected =
                            icon == item,
                        onClick = {
                            icon = item
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Renk",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                TrackTone.entries.forEach {
                        item ->

                    ToneChoice(
                        tone = item,
                        selected =
                            tone == item,
                        onClick = {
                            tone = item
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            SectionTitle(
                text = "Son yapılma tarihi"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "İsteğe bağlı",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color = MutedText
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = lastDateText,
                onValueChange = {
                    lastDateText = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "GG.AA.YYYY"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = premiumTextFieldColors()
            )

            if (!isPeriodTrack) {

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                SectionTitle(
                    text = "Tekrar aralığı"
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "İsteğe bağlı",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color = MutedText
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = repeatIntervalText,
                    onValueChange = { value ->
                        repeatIntervalText =
                            value.filter {
                                it.isDigit()
                            }
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Örn. 7"
                        )
                    },
                    suffix = {
                        if (
                            repeatIntervalText
                                .isNotEmpty()
                        ) {
                            Text(
                                text = "gün"
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    shape =
                        RoundedCornerShape(18.dp),
                    colors =
                        premiumTextFieldColors()
                )
            } else {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(18.dp),
                    color =
                        CardPink.copy(alpha = 0.60f)
                ) {
                    Text(
                        text =
                            "Regl başlangıcı yalnızca tarih geçmişini tutar. Tahmin veya gecikme yorumu yapılmaz.",
                        modifier =
                            Modifier.padding(16.dp),
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color = DarkText
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text =
                    "Veriler bu cihazda saklanır.",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color = MutedText
            )
        }
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        style =
            MaterialTheme
                .typography
                .titleMedium,
        color = DarkText,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SuggestionRow(
    suggestion: StarterSuggestion,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SurfacePaper,
        border = BorderStroke(
            width = 1.dp,
            color =
                SoftBorder.copy(alpha = 0.75f)
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
                color =
                    suggestion.tone.color()
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            suggestion
                                .icon
                                .vector(),
                        contentDescription = null,
                        tint = Plum,
                        modifier =
                            Modifier.size(19.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = suggestion.name,
                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SelectionPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            Plum
        } else {
            Color.Transparent
        },
        border = if (selected) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = SoftBorder
            )
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 9.dp
            ),
            style =
                MaterialTheme
                    .typography
                    .labelMedium,
            color = if (selected) {
                Color.White
            } else {
                MutedText
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun IconChoice(
    icon: TrackIcon,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = if (selected) {
            CardPink
        } else {
            SurfacePaper
        },
        border = BorderStroke(
            width =
                if (selected) 1.5.dp
                else 1.dp,
            color = if (selected) {
                Plum.copy(alpha = 0.55f)
            } else {
                SoftBorder
            }
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon.vector(),
                contentDescription = null,
                modifier = Modifier.size(21.dp),
                tint = Plum
            )
        }
    }
}

@Composable
private fun ToneChoice(
    tone: TrackTone,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        color = tone.color(),
        border = if (selected) {
            BorderStroke(
                width = 2.dp,
                color = Plum
            )
        } else {
            BorderStroke(
                width = 1.dp,
                color = SoftBorder
            )
        }
    ) {}
}

@Composable
private fun premiumTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor =
            SurfacePaper,
        unfocusedContainerColor =
            SurfacePaper,
        focusedBorderColor =
            Plum.copy(alpha = 0.55f),
        unfocusedBorderColor =
            SoftBorder,
        focusedTextColor = DarkText,
        unfocusedTextColor = DarkText,
        cursorColor = Plum
    )

private fun TrackTone.color(): Color {
    return when (this) {
        TrackTone.PINK -> CardPink
        TrackTone.SAGE -> CardSage
        TrackTone.YELLOW -> CardYellow
    }
}

private fun TrackIcon.vector(): ImageVector {
    return when (this) {
        TrackIcon.PERIOD ->
            Icons.Outlined.WaterDrop

        TrackIcon.PLANT ->
            Icons.Outlined.LocalFlorist

        TrackIcon.BED ->
            Icons.Outlined.Bed

        TrackIcon.COFFEE ->
            Icons.Outlined.Coffee

        TrackIcon.FILTER ->
            Icons.Outlined.FilterAlt

        TrackIcon.OTHER ->
            Icons.Outlined.MoreHoriz
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFBF7F2,
    heightDp = 900
)
@Composable
private fun NewTrackScreenPreview() {
    EnSonTheme {
        NewTrackScreen()
    }
}