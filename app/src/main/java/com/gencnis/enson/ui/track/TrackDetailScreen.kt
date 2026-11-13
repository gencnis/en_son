package com.gencnis.enson.ui.track

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Unarchive
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gencnis.enson.data.local.TrackEntity
import com.gencnis.enson.data.local.TrackRecordEntity
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import com.gencnis.enson.ui.common.DateField
import com.gencnis.enson.ui.common.PastDatePickerDialog
import com.gencnis.enson.ui.common.color
import com.gencnis.enson.ui.common.formatEpochDay
import com.gencnis.enson.ui.common.scheduleStatus
import com.gencnis.enson.ui.common.vector
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.theme.Fraunces
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.OverdueTerracotta
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.SoftBorder
import com.gencnis.enson.ui.theme.SurfacePaper
import com.gencnis.enson.ui.theme.WarmCream

private const val NOTE_MAX_LENGTH = 120

@Composable
fun TrackDetailScreen(
    track: TrackEntity,
    records: List<TrackRecordEntity>,
    todayEpochDay: Long,
    onBack: () -> Unit = {},
    onEditTrack: () -> Unit = {},
    onToggleArchive: () -> Unit = {},
    onDeleteTrack: () -> Unit = {},
    onAddRecord: (dateEpochDay: Long, note: String?) -> Unit =
        { _, _ -> },
    onUpdateRecord: (TrackRecordEntity) -> Unit = {},
    onDeleteRecord: (TrackRecordEntity) -> Unit = {}
) {
    // Newest first; the latest event is always derived from history.
    val history = remember(records) {
        records.sortedWith(
            compareByDescending<TrackRecordEntity> {
                it.dateEpochDay
            }.thenByDescending {
                it.id
            }
        )
    }

    val latest = history.firstOrNull()

    val elapsedDays =
        latest?.let {
            (todayEpochDay - it.dateEpochDay)
                .coerceAtLeast(0)
                .toInt()
        }

    var showAddRecord by rememberSaveable {
        mutableStateOf(false)
    }

    var editingRecordId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var confirmDelete by rememberSaveable {
        mutableStateOf(false)
    }

    val editingRecord =
        editingRecordId?.let { id ->
            history.firstOrNull { it.id == id }
        }

    Scaffold(
        containerColor = WarmCream,
        bottomBar = {
            Surface(
                color = WarmCream
            ) {
                Button(
                    onClick = {
                        showAddRecord = true
                    },
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
                        containerColor = Plum
                    )
                ) {
                    Text(
                        text = "Yeni kayıt ekle",
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
                top = 8.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onToggleArchive
                    ) {
                        Icon(
                            imageVector = if (track.isArchived) {
                                Icons.Outlined.Unarchive
                            } else {
                                Icons.Outlined.Archive
                            },
                            contentDescription =
                                if (track.isArchived) {
                                    "Arşivden çıkar"
                                } else {
                                    "Arşivle"
                                },
                            tint = DarkText
                        )
                    }

                    IconButton(
                        onClick = onEditTrack
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.Edit,
                            contentDescription =
                                "Takibi düzenle",
                            tint = DarkText
                        )
                    }

                    IconButton(
                        onClick = {
                            confirmDelete = true
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.DeleteOutline,
                            contentDescription =
                                "Takibi sil",
                            tint = DarkText
                        )
                    }
                }
            }

            item {
                SummaryCard(
                    track = track,
                    latestDateEpochDay =
                        latest?.dateEpochDay,
                    elapsedDays = elapsedDays,
                    typicalGapDays =
                        if (track.type == TrackType.PERIOD_START) {
                            // No cycle interpretation for periods.
                            null
                        } else {
                            typicalGapDays(history)
                        }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 18.dp,
                            bottom = 2.dp
                        ),
                    verticalAlignment =
                        Alignment.Bottom
                ) {
                    Text(
                        text = "Geçmiş",
                        modifier = Modifier.weight(1f),
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (history.isNotEmpty()) {
                        Text(
                            text = "${history.size} kayıt",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color = MutedText
                        )
                    }
                }
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        text =
                            "Henüz kayıt yok. İlk kaydı eklediğinde burada görünecek.",
                        modifier = Modifier.padding(
                            vertical = 12.dp
                        ),
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        color = MutedText
                    )
                }
            } else {
                items(
                    items = history,
                    key = { it.id }
                ) { record ->
                    RecordRow(
                        record = record,
                        todayEpochDay = todayEpochDay,
                        onEdit = {
                            editingRecordId = record.id
                        },
                        onDelete = {
                            onDeleteRecord(record)
                        }
                    )
                }
            }
        }
    }

    if (showAddRecord) {
        RecordDialog(
            title = if (
                track.type == TrackType.PERIOD_START
            ) {
                "Başlangıç ekle"
            } else {
                "Yeni kayıt"
            },
            initialDateEpochDay = todayEpochDay,
            initialNote = "",
            todayEpochDay = todayEpochDay,
            takenDates = history.associateBy {
                it.dateEpochDay
            },
            onDismiss = {
                showAddRecord = false
            },
            onSave = { date, note ->
                showAddRecord = false
                onAddRecord(date, note)
            },
            onEditExisting = { existing ->
                showAddRecord = false
                editingRecordId = existing.id
            }
        )
    }

    if (editingRecord != null) {
        RecordDialog(
            title = "Kaydı düzenle",
            initialDateEpochDay =
                editingRecord.dateEpochDay,
            initialNote =
                editingRecord.note.orEmpty(),
            todayEpochDay = todayEpochDay,
            takenDates = history
                .filter {
                    it.id != editingRecord.id
                }
                .associateBy {
                    it.dateEpochDay
                },
            onDismiss = {
                editingRecordId = null
            },
            onSave = { date, note ->
                editingRecordId = null
                onUpdateRecord(
                    editingRecord.copy(
                        dateEpochDay = date,
                        note = note
                    )
                )
            },
            onEditExisting = { existing ->
                editingRecordId = existing.id
            }
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = {
                confirmDelete = false
            },
            containerColor = SurfacePaper,
            title = {
                Text(
                    text = "Takip silinsin mi?",
                    color = DarkText
                )
            },
            text = {
                Text(
                    text = buildString {
                        append("“${track.name}”")
                        if (history.isNotEmpty()) {
                            append(
                                " ve tüm geçmişi (${history.size} kayıt)"
                            )
                        }
                        append(
                            " kalıcı olarak silinecek. Bu işlem geri alınamaz."
                        )
                    },
                    color = DarkText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        onDeleteTrack()
                    }
                ) {
                    Text(
                        text = "Sil",
                        color = OverdueTerracotta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                    }
                ) {
                    Text(
                        text = "Vazgeç",
                        color = Plum
                    )
                }
            }
        )
    }
}

@Composable
private fun SummaryCard(
    track: TrackEntity,
    latestDateEpochDay: Long?,
    elapsedDays: Int?,
    typicalGapDays: Int?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = track.tone.color()
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
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
                                Modifier.size(23.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.name,
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = track.category.label,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color = MutedText
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(26.dp)
            )

            when (elapsedDays) {
                null -> {
                    Text(
                        text = "Henüz kayıt yok",
                        fontFamily = Fraunces,
                        color = DarkText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                0, 1 -> {
                    Text(
                        text = if (elapsedDays == 0) {
                            "Bugün"
                        } else {
                            "Dün"
                        },
                        fontFamily = Fraunces,
                        color = DarkText,
                        fontSize = 48.sp,
                        lineHeight = 52.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                else -> {
                    Text(
                        text = elapsedDays.toString(),
                        fontFamily = Fraunces,
                        color = DarkText,
                        fontSize = 72.sp,
                        lineHeight = 72.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "gün geçti",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        color = MutedText
                    )
                }
            }

            latestDateEpochDay?.let {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Son kayıt  ·  ${formatEpochDay(it)}",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color = DarkText
                )
            }

            typicalGapDays?.let {
                Text(
                    text = if (it == 1) {
                        "Genelde her gün"
                    } else {
                        "Genelde $it günde bir"
                    },
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color = MutedText
                )
            }

            scheduleStatus(
                type = track.type,
                repeatIntervalDays =
                    track.repeatIntervalDays,
                elapsedDays = elapsedDays
            )?.let { status ->
                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfacePaper.copy(
                        alpha = 0.72f
                    )
                ) {
                    Text(
                        text = status.text,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,
                        color = if (status.isDue) {
                            OverdueTerracotta
                        } else {
                            DarkText
                        },
                        fontWeight = if (status.isDue) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordRow(
    record: TrackRecordEntity,
    todayEpochDay: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SurfacePaper,
        border = BorderStroke(
            width = 1.dp,
            color = SoftBorder.copy(alpha = 0.75f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 12.dp,
                bottom = 12.dp,
                end = 4.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formatEpochDay(
                        record.dateEpochDay
                    ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    color = DarkText,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = daysAgoText(
                        todayEpochDay -
                                record.dateEpochDay
                    ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color = MutedText
                )

                record.note?.let {
                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = it,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color = DarkText
                    )
                }
            }

            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Kaydı düzenle",
                    tint = MutedText,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.DeleteOutline,
                    contentDescription = "Kaydı sil",
                    tint = MutedText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RecordDialog(
    title: String,
    initialDateEpochDay: Long,
    initialNote: String,
    todayEpochDay: Long,
    takenDates: Map<Long, TrackRecordEntity>,
    onDismiss: () -> Unit,
    onSave: (dateEpochDay: Long, note: String?) -> Unit,
    onEditExisting: (TrackRecordEntity) -> Unit
) {
    var dateEpochDay by rememberSaveable(
        initialDateEpochDay
    ) {
        mutableLongStateOf(initialDateEpochDay)
    }

    var note by rememberSaveable(
        initialNote
    ) {
        mutableStateOf(initialNote)
    }

    var showPicker by rememberSaveable {
        mutableStateOf(false)
    }

    // One record per track per day in this version.
    val existing = takenDates[dateEpochDay]

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePaper,
        title = {
            Text(
                text = title,
                color = DarkText
            )
        },
        text = {
            Column {
                DateField(
                    label = "Tarih",
                    epochDay = dateEpochDay,
                    placeholder = "",
                    containerColor = WarmCream,
                    onClick = {
                        showPicker = true
                    }
                )

                if (existing != null) {
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            "Bu tarihte zaten bir kayıt var.",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color = OverdueTerracotta
                    )

                    TextButton(
                        onClick = {
                            onEditExisting(existing)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Mevcut kaydı düzenle",
                            color = Plum,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it.take(NOTE_MAX_LENGTH)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "Not (isteğe bağlı)"
                        )
                    },
                    supportingText = {
                        Text(
                            text =
                                "${note.length}/$NOTE_MAX_LENGTH"
                        )
                    },
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                Plum.copy(alpha = 0.55f),
                            unfocusedBorderColor =
                                SoftBorder,
                            focusedLabelColor = Plum,
                            cursorColor = Plum
                        )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        dateEpochDay,
                        note.trim().takeIf {
                            it.isNotEmpty()
                        }
                    )
                },
                enabled = existing == null
            ) {
                Text(
                    text = "Kaydet",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Vazgeç"
                )
            }
        }
    )

    if (showPicker) {
        PastDatePickerDialog(
            initialEpochDay = dateEpochDay,
            todayEpochDay = todayEpochDay,
            onDismiss = {
                showPicker = false
            },
            onConfirm = {
                dateEpochDay = it
                showPicker = false
            }
        )
    }
}

// Median gap between entries; needs at least three to mean anything.
internal fun typicalGapDays(
    records: List<TrackRecordEntity>
): Int? {
    val gaps = records
        .map { it.dateEpochDay }
        .sorted()
        .zipWithNext { a, b -> (b - a).toInt() }
        .filter { it > 0 }
        .sorted()

    if (gaps.size < 2) {
        return null
    }

    return gaps[gaps.size / 2]
}

private fun daysAgoText(
    days: Long
): String {
    return when {
        days <= 0L -> "Bugün"
        days == 1L -> "Dün"
        else -> "$days gün önce"
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFBF7F2,
    heightDp = 900
)
@Composable
private fun TrackDetailPreview() {
    EnSonTheme {
        TrackDetailScreen(
            track = TrackEntity(
                id = 1,
                name = "Çiçek sulama",
                category = TrackCategory.HOME,
                type = TrackType.NORMAL,
                icon = TrackIcon.PLANT,
                tone = TrackTone.SAGE,
                repeatIntervalDays = 3
            ),
            records = listOf(
                TrackRecordEntity(
                    id = 1,
                    trackId = 1,
                    dateEpochDay = 20_706,
                    note = "Salondaki saksılar"
                ),
                TrackRecordEntity(
                    id = 2,
                    trackId = 1,
                    dateEpochDay = 20_710
                )
            ),
            todayEpochDay = 20_715
        )
    }
}
