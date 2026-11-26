package com.dabskutz.kalkulator.ui.calculator

import com.dabskutz.kalkulator.data.history.HistoryEntry
import com.dabskutz.kalkulator.data.preferences.ThemeMode

/**
 * UI State for the Calculator screen and application theme.
 *
 * [expression] is the RAW computational string (digits, operators, decimal points).
 *              It never contains thousands separators.
 * [displayExpression] is the FORMATTED string shown to the user (with thousands dots).
 * [result] is the RAW result string.
 * [displayResult] is the FORMATTED result string shown to the user.
 */
data class CalculatorState(
    val expression: String = "",
    val displayExpression: String = "",
    val result: String = "0",
    val displayResult: String = "0",
    val history: List<HistoryEntry> = emptyList(),
    val error: String? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val showClearHistoryDialog: Boolean = false
)
