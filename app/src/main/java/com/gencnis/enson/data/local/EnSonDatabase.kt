package com.gencnis.enson.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TrackEntity::class,
        TrackRecordEntity::class
    ],
    version = 2,
    exportSchema = true
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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}

// 1 → 2: tracks can be archived.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE tracks ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0"
        )
    }
}
