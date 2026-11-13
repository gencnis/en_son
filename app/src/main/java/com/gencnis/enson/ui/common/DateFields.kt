package com.gencnis.enson.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gencnis.enson.ui.theme.DarkText
import com.gencnis.enson.ui.theme.MutedText
import com.gencnis.enson.ui.theme.Plum
import com.gencnis.enson.ui.theme.SoftBorder
import com.gencnis.enson.ui.theme.SurfacePaper
import java.util.Locale

/**
 * A tappable field that shows a date and opens a picker.
 * With [onClear] the date is optional and can be removed.
 */
@Composable
fun DateField(
    label: String,
    epochDay: Long?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SurfacePaper,
    onClear: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(
            width = 1.dp,
            color = SoftBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 10.dp,
                bottom = 10.dp,
                end = 4.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,
                    color = MutedText
                )

                Text(
                    text = epochDay
                        ?.let { formatEpochDay(it) }
                        ?: placeholder,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    color = if (epochDay != null) {
                        DarkText
                    } else {
                        MutedText
                    },
                    fontWeight = if (epochDay != null) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    }
                )
            }

            if (epochDay != null && onClear != null) {
                IconButton(
                    onClick = onClear
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Tarihi kaldır",
                        tint = MutedText
                    )
                }
            } else {
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.CalendarMonth,
                        contentDescription = "Tarih seç",
                        tint = Plum
                    )
                }
            }
        }
    }
}

/**
 * Turkish calendar where only today and earlier can be picked:
 * an event can't be recorded before it has happened.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastDatePickerDialog(
    initialEpochDay: Long,
    todayEpochDay: Long,
    onDismiss: () -> Unit,
    onConfirm: (epochDay: Long) -> Unit
) {
    // Built directly (not remembered via rememberDatePickerState)
    // so the calendar is Turkish whatever the device language.
    val pickerState = remember(todayEpochDay) {
        DatePickerState(
            locale = Locale.forLanguageTag("tr-TR"),
            initialSelectedDateMillis =
                initialEpochDay.coerceAtMost(
                    todayEpochDay
                ) * MILLIS_PER_DAY,
            selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(
                        utcTimeMillis: Long
                    ): Boolean {
                        return utcTimeMillis /
                                MILLIS_PER_DAY <=
                                todayEpochDay
                    }
                }
        )
    }

    val pickerColors =
        DatePickerDefaults.colors(
            containerColor = SurfacePaper
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = pickerColors,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState
                        .selectedDateMillis
                        ?.let {
                            onConfirm(
                                it / MILLIS_PER_DAY
                            )
                        }
                        ?: onDismiss()
                }
            ) {
                Text(
                    text = "Tamam"
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
    ) {
        DatePicker(
            state = pickerState,
            colors = pickerColors,
            showModeToggle = false,
            title = {
                Text(
                    text = "Tarih seç",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 12.dp,
                        top = 16.dp
                    ),
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    color = MutedText
                )
            },
            headline = {
                Text(
                    text = pickerState
                        .selectedDateMillis
                        ?.let {
                            formatEpochDay(
                                it / MILLIS_PER_DAY
                            )
                        }
                        ?: "",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    ),
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    color = DarkText
                )
            }
        )
    }
}
