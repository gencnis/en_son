package com.gencnis.enson.data.local

import androidx.room.TypeConverter
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType

class RoomConverters {

    @TypeConverter
    fun trackCategoryToString(
        value: TrackCategory
    ): String {
        return value.name
    }

    @TypeConverter
    fun stringToTrackCategory(
        value: String
    ): TrackCategory {
        return TrackCategory.valueOf(value)
    }

    @TypeConverter
    fun trackTypeToString(
        value: TrackType
    ): String {
        return value.name
    }

    @TypeConverter
    fun stringToTrackType(
        value: String
    ): TrackType {
        return TrackType.valueOf(value)
    }

    @TypeConverter
    fun trackIconToString(
        value: TrackIcon
    ): String {
        return value.name
    }

    @TypeConverter
    fun stringToTrackIcon(
        value: String
    ): TrackIcon {
        return TrackIcon.valueOf(value)
    }

    @TypeConverter
    fun trackToneToString(
        value: TrackTone
    ): String {
        return value.name
    }

    @TypeConverter
    fun stringToTrackTone(
        value: String
    ): TrackTone {
        return TrackTone.valueOf(value)
    }
}