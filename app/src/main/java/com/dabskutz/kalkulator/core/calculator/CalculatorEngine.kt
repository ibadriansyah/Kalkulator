package com.dabskutz.kalkulator.core.calculator

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Pure calculation engine using [BigDecimal] for arbitrary-precision decimal arithmetic.
 * Evaluates expressions using [ExpressionParser] with proper operator precedence.
 */
object CalculatorEngine {

    val DECIMAL_MATH: RoundingMode = RoundingMode.HALF_EVEN
    const val DEFAULT_SCALE: Int = 34

    class DivisionByZeroException : ArithmeticException("Cannot divide by zero")
    class InvalidExpressionException(message: String) : IllegalArgumentException(message)

    fun evaluate(expression: String): Result<BigDecimal> {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return Result.success(BigDecimal.ZERO)
        }
        val parser = ExpressionParser(trimmed)
        return try {
            parser.parse()
        } catch (e: DivisionByZeroException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(InvalidExpressionException(e.message ?: "Invalid expression"))
        }
    }

    fun safeDivide(a: BigDecimal, b: BigDecimal): Result<BigDecimal> {
        return if (b.compareTo(BigDecimal.ZERO) == 0) {
            Result.failure(DivisionByZeroException())
        } else {
            Result.success(a.divide(b, DEFAULT_SCALE, DECIMAL_MATH))
        }
    }

    fun formatResult(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }
        val stripped = value.stripTrailingZeros()
        val abs = stripped.abs()
        val isVeryLarge = abs >= BigDecimal("1e15")
        val isVerySmall = abs < BigDecimal("1e-6") && abs.compareTo(BigDecimal.ZERO) != 0

        return if (isVeryLarge || isVerySmall) {
            stripped.toEngineeringString()
        } else {
            stripped.toPlainString()
        }
    }
}
