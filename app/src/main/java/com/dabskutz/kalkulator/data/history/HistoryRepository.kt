package com.dabskutz.kalkulator.data.history

import com.dabskutz.kalkulator.data.preferences.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that persists calculation history using DataStore (JSON) and
 * exposes it as a StateFlow for the UI.
 */
@Singleton
class HistoryRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    companion object {
        const val MAX_ENTRIES = 100
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _entries = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val entries: StateFlow<List<HistoryEntry>> = _entries.asStateFlow()

    init {
        scope.launch {
            try {
                val initial = dataStoreManager.historyFlow.first()
                _entries.value = initial
            } catch (_: Exception) {}

            dataStoreManager.historyFlow.collect { loaded ->
                _entries.value = loaded
            }
        }
    }

    suspend fun addEntry(expression: String, result: BigDecimal) {
        val entry = HistoryEntry(
            id = System.currentTimeMillis(),
            expression = expression,
            result = result,
            timestamp = Instant.now()
        )
        val current = _entries.value.toMutableList()
        current.add(0, entry)
        val trimmed = if (current.size > MAX_ENTRIES) current.take(MAX_ENTRIES) else current
        _entries.value = trimmed
        dataStoreManager.setHistory(trimmed)
    }

    suspend fun deleteEntry(entryId: Long) {
        val updated = _entries.value.filter { it.id != entryId }
        _entries.value = updated
        dataStoreManager.setHistory(updated)
    }

    suspend fun clearAll() {
        _entries.value = emptyList()
        dataStoreManager.setHistory(emptyList())
    }
}
