package com.gencnis.enson.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_records",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["trackId"]
        ),
        Index(
            value = ["trackId", "dateEpochDay"],
            unique = true
        )
    ]
)
data class TrackRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val trackId: Long,

    val dateEpochDay: Long,

    val note: String? = null
)