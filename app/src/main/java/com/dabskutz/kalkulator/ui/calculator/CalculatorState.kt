package com.dabskutz.kalkulator.ui.calculator

import com.dabskutz.kalkulator.data.history.HistoryEntry
import com.dabskutz.kalkulator.data.preferences.ThemeMode

/**
 * UI State for the Calculator screen and application theme.
 */
data class CalculatorState(
    val expression: String = "",
    val result: String = "0",
    val history: List<HistoryEntry> = emptyList(),
    val error: String? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val showClearHistoryDialog: Boolean = false
)
