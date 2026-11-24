package com.dabskutz.kalkulator.data.history

import java.math.BigDecimal
import java.time.Instant

/**
 * Domain model for a single saved calculation.
 */
data class HistoryEntry(
    val id: Long = System.currentTimeMillis(),
    val expression: String,
    val result: BigDecimal,
    val timestamp: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HistoryEntry) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
