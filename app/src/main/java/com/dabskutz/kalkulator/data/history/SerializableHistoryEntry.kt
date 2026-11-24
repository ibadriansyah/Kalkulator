package com.dabskutz.kalkulator.data.history

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Instant

/**
 * JSON-serialisable version of [HistoryEntry] used to persist history
 * via DataStore.
 */
@Serializable
internal data class SerializableHistoryEntry(
    val id: Long,
    val expression: String,
    val result: String,
    val timestamp: Long
) {
    fun toModel(): HistoryEntry = HistoryEntry(
        id = id,
        expression = expression,
        result = BigDecimal(result),
        timestamp = Instant.ofEpochMilli(timestamp)
    )

    companion object {
        fun fromModel(entry: HistoryEntry): SerializableHistoryEntry {
            return SerializableHistoryEntry(
                id = entry.id,
                expression = entry.expression,
                result = entry.result.toPlainString(),
                timestamp = entry.timestamp.toEpochMilli()
            )
        }
    }
}
