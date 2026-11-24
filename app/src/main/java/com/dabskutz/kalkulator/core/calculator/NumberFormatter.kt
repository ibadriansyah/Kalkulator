package com.dabskutz.kalkulator.core.calculator

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Number formatting utilities for the calculator display.
 */
object NumberFormatter {

    private const val MAX_DISPLAY_CHARS = 14
    private const val MAX_SCALE = 34

    fun format(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val abs = value.abs()
        val isLarge = abs >= BigDecimal("1e14") || (abs < BigDecimal("1e-6") && abs.compareTo(BigDecimal.ZERO) != 0)
        val rounded = value.setScale(MAX_SCALE, RoundingMode.HALF_EVEN)
        val stripped = rounded.stripTrailingZeros()

        if (isLarge) {
            val eng = stripped.toEngineeringString()
            return if (eng.length > MAX_DISPLAY_CHARS) eng.take(MAX_DISPLAY_CHARS) else eng
        }

        val plain = stripped.toPlainString()
        return if (plain.length <= MAX_DISPLAY_CHARS) {
            plain
        } else {
            val eng = stripped.toEngineeringString()
            if (eng.length <= MAX_DISPLAY_CHARS) eng else plain.take(MAX_DISPLAY_CHARS)
        }
    }

    fun needsCompactDisplay(value: BigDecimal): Boolean {
        val abs = value.abs()
        return abs >= BigDecimal("1e14") || (abs < BigDecimal("1e-6") && abs.compareTo(BigDecimal.ZERO) != 0)
    }
}
