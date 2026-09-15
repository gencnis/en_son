package com.gencnis.enson.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        TrackEntity::class,
        TrackRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    RoomConverters::class
)
abstract class EnSonDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    companion object {

        @Volatile
        private var INSTANCE: EnSonDatabase? = null

        fun getInstance(
            context: Context
        ): EnSonDatabase {
            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EnSonDatabase::class.java,
                    "en_son.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}