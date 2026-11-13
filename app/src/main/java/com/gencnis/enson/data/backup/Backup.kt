package com.gencnis.enson.data.backup

import androidx.room.withTransaction
import com.gencnis.enson.data.enumOrDefault
import com.gencnis.enson.data.local.EnSonDatabase
import com.gencnis.enson.data.local.TrackEntity
import com.gencnis.enson.data.local.TrackRecordEntity
import com.gencnis.enson.model.TrackCategory
import com.gencnis.enson.model.TrackIcon
import com.gencnis.enson.model.TrackTone
import com.gencnis.enson.model.TrackType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// A backup is a plain JSON file the user saves wherever they like.
// Nothing is uploaded; the app only reads and writes that one file.

private const val APP_ID = "com.gencnis.enson"
private const val FORMAT_VERSION = 1

private const val NAME_MAX_LENGTH = 60
private const val NOTE_MAX_LENGTH = 120

data class BackupData(
    val tracks: List<TrackEntity>,
    val records: List<TrackRecordEntity>
)

class BackupFormatException(
    message: String
) : Exception(message)

suspend fun EnSonDatabase.readBackup(): BackupData {
    val dao = trackDao()

    return BackupData(
        tracks = dao.getAllTracks(),
        records = dao.getAllRecords()
    )
}

// Replaces everything on the device with the backup's contents.
suspend fun EnSonDatabase.restoreBackup(
    data: BackupData
) {
    val dao = trackDao()

    withTransaction {
        dao.deleteAllTracks()
        dao.insertTracks(data.tracks)
        dao.insertRecords(data.records)
    }
}

fun BackupData.toJson(): String {
    val root = JSONObject()

    root.put("app", APP_ID)
    root.put("format", FORMAT_VERSION)
    root.put(
        "exportedAtEpochMillis",
        System.currentTimeMillis()
    )

    root.put(
        "tracks",
        JSONArray().apply {
            tracks.forEach { track ->
                put(
                    JSONObject().apply {
                        put("id", track.id)
                        put("name", track.name)
                        put("category", track.category.name)
                        put("type", track.type.name)
                        put("icon", track.icon.name)
                        put("tone", track.tone.name)
                        track.repeatIntervalDays?.let {
                            put("repeatIntervalDays", it)
                        }
                        put(
                            "createdAtEpochMillis",
                            track.createdAtEpochMillis
                        )
                        put("isArchived", track.isArchived)
                    }
                )
            }
        }
    )

    root.put(
        "records",
        JSONArray().apply {
            records.forEach { record ->
                put(
                    JSONObject().apply {
                        put("id", record.id)
                        put("trackId", record.trackId)
                        put("dateEpochDay", record.dateEpochDay)
                        record.note?.let {
                            put("note", it)
                        }
                    }
                )
            }
        }
    )

    return root.toString(2)
}

/**
 * Reads a backup file, tolerating values a newer app version may add
 * (unknown icons fall back to defaults) but rejecting anything that is
 * not an En Son backup.
 */
fun parseBackup(
    text: String
): BackupData {
    try {
        val root = JSONObject(text)

        if (root.optString("app") != APP_ID) {
            throw BackupFormatException(
                "Not an En Son backup"
            )
        }

        if (root.optInt("format", -1) > FORMAT_VERSION) {
            throw BackupFormatException(
                "Backup is from a newer version"
            )
        }

        val trackArray = root.getJSONArray("tracks")

        val tracks = (0 until trackArray.length())
            .map { trackArray.getJSONObject(it) }
            .mapNotNull { json ->
                val name = json
                    .getString("name")
                    .trim()
                    .take(NAME_MAX_LENGTH)

                if (name.isEmpty()) {
                    return@mapNotNull null
                }

                val type = enumOrDefault(
                    json.optString("type"),
                    TrackType.NORMAL
                )

                TrackEntity(
                    id = json.getLong("id"),
                    name = name,
                    category = enumOrDefault(
                        json.optString("category"),
                        TrackCategory.OTHER
                    ),
                    type = type,
                    icon = enumOrDefault(
                        json.optString("icon"),
                        TrackIcon.OTHER
                    ),
                    tone = enumOrDefault(
                        json.optString("tone"),
                        TrackTone.PINK
                    ),
                    repeatIntervalDays =
                        if (
                            type == TrackType.PERIOD_START ||
                            !json.has("repeatIntervalDays")
                        ) {
                            null
                        } else {
                            json.getInt("repeatIntervalDays")
                                .takeIf { it > 0 }
                        },
                    createdAtEpochMillis = json.optLong(
                        "createdAtEpochMillis",
                        System.currentTimeMillis()
                    ),
                    isArchived = json.optBoolean(
                        "isArchived",
                        false
                    )
                )
            }
            .distinctBy { it.id }

        val trackIds = tracks.map { it.id }.toSet()

        val recordArray = root.getJSONArray("records")

        val records = (0 until recordArray.length())
            .map { recordArray.getJSONObject(it) }
            .map { json ->
                TrackRecordEntity(
                    id = json.getLong("id"),
                    trackId = json.getLong("trackId"),
                    dateEpochDay = json.getLong("dateEpochDay"),
                    note = json
                        .optString("note")
                        .trim()
                        .take(NOTE_MAX_LENGTH)
                        .takeIf { it.isNotEmpty() }
                )
            }
            .filter { it.trackId in trackIds }
            // One record per track per day, as in the app.
            .distinctBy { it.trackId to it.dateEpochDay }
            .distinctBy { it.id }

        return BackupData(
            tracks = tracks,
            records = records
        )

    } catch (e: JSONException) {
        throw BackupFormatException(
            e.message ?: "Invalid backup"
        )
    }
}
