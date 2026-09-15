package com.gencnis.enson.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType

@Entity(
    tableName = "tracks"
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val category: TrackCategory,

    val type: TrackType,

    val icon: TrackIcon,

    val tone: TrackTone,

    val repeatIntervalDays: Int? = null,

    val createdAtEpochMillis: Long =
        System.currentTimeMillis()
)