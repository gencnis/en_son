package com.gencnis.enson.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query(
        """
        SELECT *
        FROM tracks
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun observeTracks(): Flow<List<TrackEntity>>

    @Query(
        """
        SELECT *
        FROM tracks
        WHERE id = :trackId
        LIMIT 1
        """
    )
    fun observeTrack(
        trackId: Long
    ): Flow<TrackEntity?>

    @Insert
    suspend fun insertTrack(
        track: TrackEntity
    ): Long

    @Update
    suspend fun updateTrack(
        track: TrackEntity
    )

    @Delete
    suspend fun deleteTrack(
        track: TrackEntity
    )

    @Query(
        """
        SELECT *
        FROM track_records
        ORDER BY dateEpochDay DESC, id DESC
        """
    )
    fun observeAllRecords(): Flow<List<TrackRecordEntity>>

    @Query(
        """
        SELECT *
        FROM track_records
        WHERE trackId = :trackId
        ORDER BY dateEpochDay DESC, id DESC
        """
    )
    fun observeRecords(
        trackId: Long
    ): Flow<List<TrackRecordEntity>>

    @Query(
        """
        SELECT *
        FROM track_records
        WHERE trackId = :trackId
        ORDER BY dateEpochDay DESC, id DESC
        LIMIT 1
        """
    )
    fun observeLatestRecord(
        trackId: Long
    ): Flow<TrackRecordEntity?>

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertRecord(
        record: TrackRecordEntity
    ): Long

    @Update
    suspend fun updateRecord(
        record: TrackRecordEntity
    )

    @Delete
    suspend fun deleteRecord(
        record: TrackRecordEntity
    )

    @Query(
        """
        DELETE FROM track_records
        WHERE trackId = :trackId
        """
    )
    suspend fun deleteAllRecords(
        trackId: Long
    )

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM track_records
            WHERE trackId = :trackId
              AND dateEpochDay = :dateEpochDay
        )
        """
    )
    suspend fun recordExistsOnDate(
        trackId: Long,
        dateEpochDay: Long
    ): Boolean
}