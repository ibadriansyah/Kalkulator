package com.dabskutz.kalkulator.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dabskutz.kalkulator.data.history.HistoryEntry
import com.dabskutz.kalkulator.data.history.SerializableHistoryEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kalkulator_prefs")

private val THEME_KEY = stringPreferencesKey("theme_mode")
private val HISTORY_KEY = stringPreferencesKey("history_data")

/**
 * Thread-safe DataStore manager for app preferences and calculation history.
 */
@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val raw = prefs[THEME_KEY] ?: ThemeMode.System.name
            runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.System)
        }

    val historyFlow: Flow<List<HistoryEntry>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val json = prefs[HISTORY_KEY] ?: return@map emptyList()
            runCatching {
                val list = Json.decodeFromString<List<SerializableHistoryEntry>>(json)
                list.map { it.toModel() }
            }.getOrDefault(emptyList())
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = mode.name
        }
    }

    suspend fun setHistory(entries: List<HistoryEntry>) {
        context.dataStore.edit { prefs ->
            val serializable = entries.map { SerializableHistoryEntry.fromModel(it) }
            prefs[HISTORY_KEY] = Json.encodeToString(serializable)
        }
    }
}
