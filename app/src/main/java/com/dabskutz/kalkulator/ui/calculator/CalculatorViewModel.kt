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

    fun onDigit(digit: String) {
        _state.update { curr ->
            val newExpr = if (curr.expression == "0") digit else curr.expression + digit
            val preview = previewResult(newExpr)
            curr.copy(
                expression = newExpr,
                result = preview ?: curr.result,
                error = null
            )
        }
    }

    fun onOperator(op: String) {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    return@update curr.copy(expression = "−", error = null)
                }
                return@update curr.copy(expression = "0$op", error = null)
            }
            val lastChar = expr.last()
            val newExpr = if (isOperatorChar(lastChar)) {
                expr.dropLast(1) + op
            } else {
                expr + op
            }
            curr.copy(expression = newExpr, error = null)
        }
    }

    fun onDecimal() {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty()) {
                return@update curr.copy(expression = "0.", error = null)
            }
            val lastChar = expr.last()
            if (isOperatorChar(lastChar) || lastChar == '(') {
                return@update curr.copy(expression = "$expr 0.", error = null)
            }
            val lastSegment = expr.split('+', '−', '-', '×', '*', '÷', '/', '(', ')', '%').last()
            if (!lastSegment.contains('.')) {
                curr.copy(expression = "$expr.", error = null)
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
            val preview = previewResult(newExpr)
            curr.copy(
                expression = newExpr,
                result = preview ?: curr.result,
                error = null
            )
        }
    }

    fun onPercent() {
        _state.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') {
                return@update curr
            }
            val newExpr = "$expr%"
            val preview = previewResult(newExpr)
            curr.copy(
                expression = newExpr,
                result = preview ?: curr.result,
                error = null
            )
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
            val preview = previewResult(newExpr)
            curr.copy(
                expression = newExpr,
                result = preview ?: curr.result,
                error = null
            )
        }
    }

    fun onClear() {
        _state.update {
            it.copy(
                expression = "",
                result = "0",
                error = null
            )
        }
    }

    fun onBackspace() {
        _state.update { curr ->
            if (curr.expression.isNotEmpty()) {
                val newExpr = curr.expression.dropLast(1)
                val preview = if (newExpr.isEmpty()) "0" else (previewResult(newExpr) ?: curr.result)
                curr.copy(
                    expression = newExpr,
                    result = preview,
                    error = null
                )
            } else {
                curr.copy(result = "0", error = null)
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
                val formatted = CalculatorEngine.formatResult(decimalValue)
                viewModelScope.launch {
                    historyRepository.addEntry(expr, decimalValue)
                }
                _state.update {
                    it.copy(
                        expression = formatted,
                        result = formatted,
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
        val formatted = CalculatorEngine.formatResult(entry.result)
        _state.update {
            it.copy(
                expression = entry.expression,
                result = formatted,
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

    private fun previewResult(expression: String): String? {
        return CalculatorEngine.evaluate(expression).fold(
            onSuccess = { CalculatorEngine.formatResult(it) },
            onFailure = { null }
        )
    }

    private fun isOperatorChar(c: Char): Boolean {
        return c == '+' || c == '-' || c == '−' || c == '×' || c == '*' || c == '÷' || c == '/'
    }
}
