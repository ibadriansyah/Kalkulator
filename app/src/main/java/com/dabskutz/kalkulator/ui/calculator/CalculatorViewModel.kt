package com.dabskutz.kalkulator.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dabskutz.kalkulator.core.calculator.CalculatorEngine
import com.dabskutz.kalkulator.core.calculator.NumberFormatter
import com.dabskutz.kalkulator.data.history.HistoryEntry
import com.dabskutz.kalkulator.data.history.HistoryRepository
import com.dabskutz.kalkulator.data.preferences.DataStoreManager
import com.dabskutz.kalkulator.data.preferences.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            historyRepository.entries.collect { list ->
                _state.update { it.copy(history = list) }
            }
        }
        viewModelScope.launch {
            dataStoreManager.themeModeFlow.collect { mode ->
                _state.update { it.copy(themeMode = mode) }
            }
        }
    }

    /**
     * Helper: given a new raw expression, compute display expression,
     * preview result, and display result in one shot.
     */
    private fun buildUpdate(
        curr: CalculatorState,
        newRawExpr: String,
        clearError: Boolean = true
    ): CalculatorState {
        val displayExpr = NumberFormatter.formatExpression(newRawExpr)
        val preview = previewResult(newRawExpr)
        val rawResult = preview ?: curr.result
        val displayRes = if (preview != null) {
            formatResultForDisplay(preview)
        } else {
            curr.displayResult
        }
        return curr.copy(
            expression = newRawExpr,
            displayExpression = displayExpr,
            result = rawResult,
            displayResult = displayRes,
            error = if (clearError) null else curr.error
        )
    }

    fun onDigit(digit: String) {
        _state.update { curr ->
            val newExpr = if (curr.expression == "0") digit else curr.expression + digit
            buildUpdate(curr, newExpr)
        }
    }

    fun onOperator(op: String) {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    return@update buildUpdate(curr, "−")
                }
                return@update buildUpdate(curr, "0$op")
            }
            val lastChar = expr.last()
            val newExpr = if (isOperatorChar(lastChar)) {
                expr.dropLast(1) + op
            } else {
                expr + op
            }
            buildUpdate(curr, newExpr)
        }
    }

    fun onDecimal() {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty()) {
                return@update buildUpdate(curr, "0.")
            }
            val lastChar = expr.last()
            if (isOperatorChar(lastChar) || lastChar == '(') {
                return@update buildUpdate(curr, "${expr}0.")
            }
            val lastSegment = expr.split('+', '−', '-', '×', '*', '÷', '/', '(', ')', '%').last()
            if (!lastSegment.contains('.')) {
                buildUpdate(curr, "$expr.")
            } else {
                curr
            }
        }
    }

    fun onParenthesis() {
        _state.update { curr ->
            val expr = curr.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val newExpr = if (expr.isEmpty()) {
                "("
            } else {
                val lastChar = expr.last()
                if (isOperatorChar(lastChar) || lastChar == '(') {
                    "$expr("
                } else if (openCount > closeCount && (lastChar.isDigit() || lastChar == ')' || lastChar == '%')) {
                    "$expr)"
                } else {
                    "$expr×("
                }
            }
            buildUpdate(curr, newExpr)
        }
    }

    fun onPercent() {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') {
                return@update curr
            }
            val newExpr = "$expr%"
            buildUpdate(curr, newExpr)
        }
    }

    fun onToggleSign() {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty() || expr == "0") return@update curr

            val newExpr = if (expr.startsWith("−")) {
                expr.removePrefix("−")
            } else if (expr.startsWith("-")) {
                expr.removePrefix("-")
            } else {
                "−$expr"
            }
            buildUpdate(curr, newExpr)
        }
    }

    fun onClear() {
        _state.update {
            it.copy(
                expression = "",
                displayExpression = "",
                result = "0",
                displayResult = "0",
                error = null
            )
        }
    }

    fun onBackspace() {
        _state.update { curr ->
            if (curr.expression.isNotEmpty()) {
                val newExpr = curr.expression.dropLast(1)
                if (newExpr.isEmpty()) {
                    curr.copy(
                        expression = "",
                        displayExpression = "",
                        result = "0",
                        displayResult = "0",
                        error = null
                    )
                } else {
                    buildUpdate(curr, newExpr)
                }
            } else {
                curr.copy(result = "0", displayResult = "0", error = null)
            }
        }
    }

    fun onEquals() {
        val curr = _state.value
        val expr = curr.expression.trim()
        if (expr.isEmpty()) return

        val evalResult = CalculatorEngine.evaluate(expr)
        evalResult.fold(
            onSuccess = { decimalValue ->
                val rawFormatted = CalculatorEngine.formatResult(decimalValue)
                val displayFormatted = formatResultForDisplay(rawFormatted)
                viewModelScope.launch {
                    historyRepository.addEntry(expr, decimalValue)
                }
                _state.update {
                    it.copy(
                        expression = rawFormatted,
                        displayExpression = displayFormatted,
                        result = rawFormatted,
                        displayResult = displayFormatted,
                        error = null
                    )
                }
            },
            onFailure = { exception ->
                val errorMsg = when (exception) {
                    is CalculatorEngine.DivisionByZeroException -> "Cannot divide by zero"
                    else -> "Invalid format"
                }
                _state.update { it.copy(error = errorMsg) }
            }
        )
    }

    fun onThemeChanged(mode: ThemeMode) {
        viewModelScope.launch {
            dataStoreManager.setThemeMode(mode)
        }
    }

    fun onReuseHistory(entry: HistoryEntry) {
        val rawFormatted = CalculatorEngine.formatResult(entry.result)
        val displayFormatted = formatResultForDisplay(rawFormatted)
        val displayExpr = NumberFormatter.formatExpression(entry.expression)
        _state.update {
            it.copy(
                expression = entry.expression,
                displayExpression = displayExpr,
                result = rawFormatted,
                displayResult = displayFormatted,
                error = null
            )
        }
    }

    fun onDeleteHistoryEntry(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteEntry(id)
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    // -----------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------

    private fun previewResult(expression: String): String? {
        return CalculatorEngine.evaluate(expression).fold(
            onSuccess = { CalculatorEngine.formatResult(it) },
            onFailure = { null }
        )
    }

    /**
     * Takes a raw result string (from CalculatorEngine.formatResult) and
     * returns a display-formatted version with thousands separators.
     * Engineering notation (contains 'E') is left as-is.
     */
    private fun formatResultForDisplay(raw: String): String {
        return if (raw.contains('E') || raw.contains('e')) {
            raw
        } else {
            NumberFormatter.addThousandsSeparator(raw)
        }
    }

    private fun isOperatorChar(c: Char): Boolean {
        return c == '+' || c == '-' || c == '−' || c == '×' || c == '*' || c == '÷' || c == '/'
    }
}
