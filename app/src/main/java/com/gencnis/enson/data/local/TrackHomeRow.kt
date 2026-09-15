package com.gencnis.enson.data.local

import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType

data class TrackHomeRow(
    val id: Long,
    val name: String,
    val category: TrackCategory,
    val type: TrackType,
    val icon: TrackIcon,
    val tone: TrackTone,
    val repeatIntervalDays: Int?,
    val createdAtEpochMillis: Long,
    val latestDateEpochDay: Long?
)