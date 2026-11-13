package com.gencnis.enson.ui.track

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gencnis.enson.model.StarterSuggestion
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.model.starterSuggestions
import com.gencnis.enson.ui.common.DateField
import com.gencnis.enson.ui.common.PastDatePickerDialog
import com.gencnis.enson.ui.common.color
import com.gencnis.enson.ui.common.currentEpochDay
import com.gencnis.enson.ui.common.vector
import com.gencnis.enson.ui.theme.CardPink
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.OnPlum
import com.gencnis.enson.ui.theme.OverdueTerracotta
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
    val lastDateEpochDay: Long?,
    val repeatIntervalDays: Int?
)

private const val NAME_MAX_LENGTH = 60

@Composable
fun NewTrackScreen(
    initial: NewTrackDraft? = null,
    suggestion: StarterSuggestion? = null,
    todayEpochDay: Long = currentEpochDay(),
    existingNames: Set<String> = emptySet(),
    onBack: () -> Unit = {},
    onSubmit: (NewTrackDraft) -> Unit = {}
) {
    // With an initial draft the screen edits an existing track;
    // dates are then managed from the track's history instead.
    val isEditing = initial != null

    // What the form starts from, to tell whether anything changed.
    val startDraft = remember {
        initial ?: NewTrackDraft(
            name = suggestion?.name.orEmpty(),
            category = suggestion?.category
                ?: TrackCategory.PERSONAL,
            type = suggestion?.type
                ?: TrackType.NORMAL,
            icon = suggestion?.icon
                ?: TrackIcon.OTHER,
            tone = suggestion?.tone
                ?: TrackTone.PINK,
            lastDateEpochDay = null,
            repeatIntervalDays = null
        )
    }

    var name by rememberSaveable {
        mutableStateOf(startDraft.name)
    }

    var category by rememberSaveable {
        mutableStateOf(startDraft.category)
    }

    var type by rememberSaveable {
        mutableStateOf(startDraft.type)
    }

    var icon by rememberSaveable {
        mutableStateOf(startDraft.icon)
    }

    var tone by rememberSaveable {
        mutableStateOf(startDraft.tone)
    }

    var lastDateEpochDay by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    var confirmDiscard by rememberSaveable {
        mutableStateOf(false)
    }

    var repeatIntervalText by rememberSaveable {
        mutableStateOf(
            initial
                ?.repeatIntervalDays
                ?.toString()
                .orEmpty()
        )
    }

    val isPeriodTrack =
        type == TrackType.PERIOD_START

    val canCreate =
        name.trim().isNotEmpty()

    val isDuplicateName =
        name.trim().lowercase() !=
                initial?.name?.trim()?.lowercase() &&
                existingNames.any {
                    it.trim().equals(
                        name.trim(),
                        ignoreCase = true
                    )
                }

    val currentDraft =
        NewTrackDraft(
            name = name.trim(),
            category = category,
            type = type,
            icon = icon,
            tone = tone,
            lastDateEpochDay = lastDateEpochDay,
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

    val hasChanges =
        currentDraft != startDraft.copy(
            name = startDraft.name.trim()
        )

    val requestBack = {
        if (hasChanges) {
            confirmDiscard = true
        } else {
            onBack()
        }
    }

    BackHandler(
        enabled = hasChanges
    ) {
        confirmDiscard = true
    }

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

                        onSubmit(currentDraft)
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
                        text = if (isEditing) {
                            "Kaydet"
                        } else {
                            "Takibi oluştur"
                        },
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
                    onClick = requestBack
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
                    text = if (isEditing) {
                        "Takibi düzenle"
                    } else {
                        "Yeni takip"
                    },
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    color = DarkText
                )
            }

            if (!isEditing) {

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

            }

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
                    name = it.take(NAME_MAX_LENGTH)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Örn. Diş kontrolü"
                    )
                },
                supportingText = if (isDuplicateName) {
                    {
                        Text(
                            text =
                                "Bu isimde bir takip zaten var.",
                            color = OverdueTerracotta
                        )
                    }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        capitalization =
                            KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = premiumTextFieldColors()
            )

            if (!isEditing) {

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

            AppearancePreview(
                name = name.trim(),
                icon = icon,
                tone = tone
            )

            Spacer(
                modifier = Modifier.height(18.dp)
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

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                TrackIcon.entries.forEach {
                        item ->

                    IconChoice(
                        icon = item,
                        accent = tone.color(),
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

            FlowRow(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp),
                verticalArrangement =
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

            if (!isEditing) {

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

                DateField(
                    label = "Tarih",
                    epochDay = lastDateEpochDay,
                    placeholder = "Seçilmedi",
                    onClick = {
                        showDatePicker = true
                    },
                    onClear = {
                        lastDateEpochDay = null
                    }
                )

            }

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
                            value
                                .filter {
                                    it.isDigit()
                                }
                                .take(3)
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
                    Column(
                        modifier =
                            Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = if (isEditing) {
                                    16.dp
                                } else {
                                    4.dp
                                }
                            )
                    ) {
                        Text(
                            text =
                                "Regl başlangıcı yalnızca tarih geçmişini tutar. Tahmin veya gecikme yorumu yapılmaz.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color = DarkText
                        )

                        if (!isEditing) {
                            TextButton(
                                onClick = {
                                    type = TrackType.NORMAL
                                },
                                contentPadding =
                                    PaddingValues(0.dp)
                            ) {
                                Text(
                                    text =
                                        "Normal takip olarak oluştur",
                                    color = Plum,
                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text =
                    "Veriler bu cihazda saklanır. Uygulamayı kaldırırsan kayıtların da silinir.",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color = MutedText
            )
        }
    }

    if (showDatePicker) {
        PastDatePickerDialog(
            initialEpochDay =
                lastDateEpochDay ?: todayEpochDay,
            todayEpochDay = todayEpochDay,
            onDismiss = {
                showDatePicker = false
            },
            onConfirm = {
                lastDateEpochDay = it
                showDatePicker = false
            }
        )
    }

    if (confirmDiscard) {
        AlertDialog(
            onDismissRequest = {
                confirmDiscard = false
            },
            containerColor = SurfacePaper,
            title = {
                Text(
                    text = "Değişiklikler kaydedilmesin mi?",
                    color = DarkText
                )
            },
            text = {
                Text(
                    text = if (isEditing) {
                        "Yaptığın değişiklikler kaybolacak."
                    } else {
                        "Bu takip oluşturulmayacak."
                    },
                    color = DarkText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDiscard = false
                        onBack()
                    }
                ) {
                    Text(
                        text = "Çık",
                        color = Plum,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmDiscard = false
                    }
                ) {
                    Text(
                        text = "Düzenlemeye devam et",
                        color = Plum
                    )
                }
            }
        )
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
                OnPlum
            } else {
                MutedText
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

// A small version of the home card, so choices are seen in place.
@Composable
private fun AppearancePreview(
    name: String,
    icon: TrackIcon,
    tone: TrackTone
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = tone.color()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = SurfacePaper
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon.vector(),
                        contentDescription = null,
                        tint = Plum,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(13.dp)
            )

            Text(
                text = name.ifEmpty { "Takip adı" },
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                color = if (name.isEmpty()) {
                    MutedText
                } else {
                    DarkText
                },
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IconChoice(
    icon: TrackIcon,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = if (selected) {
            accent
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
                contentDescription = icon.label,
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
        modifier = Modifier
            .size(42.dp)
            .semantics {
                contentDescription = tone.label
            },
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