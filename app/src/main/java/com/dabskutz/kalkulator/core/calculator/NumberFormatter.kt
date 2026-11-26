package com.dabskutz.kalkulator.core.calculator

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Number formatting utilities for the calculator display.
 *
 * Key design:
 * - RAW values (digits + operators + decimal point) are used for computation.
 * - DISPLAY values have thousands separators (dots) inserted per id-ID locale.
 * - [formatWithSeparators] adds dots every 3 digits from the right (integer part only).
 * - [stripSeparators] removes all dots that are thousands separators, leaving
 *   the decimal comma/point intact.
 */
object NumberFormatter {

    private const val MAX_DISPLAY_CHARS = 20
    private const val MAX_SCALE = 34
    private const val THOUSANDS_SEP = '.'   // id-ID locale: dot is thousands separator
    private const val DECIMAL_SEP = ','     // id-ID locale: comma is decimal separator

    // -----------------------------------------------------------------
    // 1. Result formatting (BigDecimal -> plain string without separators)
    // -----------------------------------------------------------------

    fun format(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val abs = value.abs()
        val isLarge = abs >= BigDecimal("1e15") ||
                (abs < BigDecimal("1e-6") && abs.compareTo(BigDecimal.ZERO) != 0)
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

    // -----------------------------------------------------------------
    // 2. Thousands separator insertion (for display)
    // -----------------------------------------------------------------

    /**
     * Inserts thousands separator dots into the integer part of a numeric string.
     * Example: "1000000" -> "1.000.000", "-123456.78" -> "-123.456,78"
     *
     * This operates on a raw numeric string (digits, optional leading minus,
     * optional decimal point). It uses [BigInteger] internally so there is no
     * overflow regardless of digit count.
     */
    fun addThousandsSeparator(raw: String): String {
        if (raw.isEmpty()) return raw

        val negative = raw.startsWith("-") || raw.startsWith("−")
        val unsigned = if (negative) raw.drop(1) else raw
        val prefix = if (negative) "−" else ""

        // Split at the decimal point (the RAW value uses '.' as decimal)
        val dotIndex = unsigned.indexOf('.')
        val intPart: String
        val decPart: String?

        if (dotIndex >= 0) {
            intPart = unsigned.substring(0, dotIndex)
            decPart = unsigned.substring(dotIndex + 1) // digits after '.'
        } else {
            intPart = unsigned
            decPart = null
        }

        // Only digits in the integer part should be grouped
        val digitsOnly = intPart.filter { it.isDigit() }
        if (digitsOnly.isEmpty()) {
            // Edge: just a decimal point like ".5"
            return if (decPart != null) "${prefix}0${THOUSANDS_SEP}$decPart" else "${prefix}0"
        }

        // Insert separator every 3 digits from the right
        val grouped = buildString {
            val len = digitsOnly.length
            for (i in 0 until len) {
                if (i > 0 && (len - i) % 3 == 0) {
                    append(THOUSANDS_SEP)
                }
                append(digitsOnly[i])
            }
        }

        return if (decPart != null) {
            "$prefix$grouped$DECIMAL_SEP$decPart"
        } else {
            "$prefix$grouped"
        }
    }

    /**
     * Formats a full calculator expression for display by inserting thousands
     * separators into every numeric segment while leaving operators, parentheses,
     * and percent signs untouched.
     *
     * Example: "100000+2500000" -> "100.000+2.500.000"
     */
    fun formatExpression(rawExpr: String): String {
        if (rawExpr.isEmpty()) return rawExpr

        val result = StringBuilder()
        var i = 0
        while (i < rawExpr.length) {
            val c = rawExpr[i]
            if (c.isDigit() || (c == '.' && i + 1 < rawExpr.length && rawExpr[i + 1].isDigit())) {
                // Collect the full number token (digits and at most one decimal point)
                val start = i
                var dotCount = if (c == '.') 1 else 0
                i++
                while (i < rawExpr.length && (rawExpr[i].isDigit() || rawExpr[i] == '.')) {
                    if (rawExpr[i] == '.') {
                        dotCount++
                        if (dotCount > 1) break
                    }
                    i++
                }
                val numToken = rawExpr.substring(start, i)
                result.append(addThousandsSeparator(numToken))
            } else {
                result.append(c)
                i++
            }
        }
        return result.toString()
    }

    // -----------------------------------------------------------------
    // 3. Strip separators (display -> raw for computation)
    // -----------------------------------------------------------------

    /**
     * Strips thousands separator dots from a formatted expression, restoring
     * the raw computational form without corrupting valid decimal points.
     *
     * In id-ID locale:
     * - If the expression contains a comma (','), the comma is the decimal separator,
     *   so all dots ('.') are thousands separators and are removed, and comma becomes '.'.
     *   Example: "1.000.000,5" -> "1000000.5", "0,5" -> "0.5"
     * - If the expression has NO comma (','):
     *   Dots are only stripped if they represent thousands separators (e.g. multiple dots
     *   like "1.000.000" or single dot with 3 digits and non-zero prefix like "100.000").
     *   Genuine decimal dots (like "0.1", "0.2", "1.25", "3.14159") are preserved intact.
     */
    fun stripSeparators(formatted: String): String {
        if (formatted.isEmpty()) return formatted

        // If it contains comma ',', comma is the decimal separator (id-ID locale)
        if (formatted.contains(DECIMAL_SEP)) {
            return formatted
                .replace(THOUSANDS_SEP.toString(), "")
                .replace(DECIMAL_SEP, '.')
        }

        // Token-based cleaning for strings without commas:
        return formatted.replace(Regex("""\d+(?:\.\d+)+""")) { matchResult ->
            val token = matchResult.value
            val dots = token.count { it == '.' }
            if (dots > 1) {
                token.replace(".", "")
            } else if (dots == 1) {
                val parts = token.split('.')
                if (parts[1].length == 3 && parts[0] != "0") {
                    parts[0] + parts[1]
                } else {
                    token
                }
            } else {
                token
            }
        }
    }

    /**
     * Formats a BigDecimal result with thousands separators for display.
     * This is used for the result line.
     */
    fun formatResultDisplay(value: BigDecimal): String {
        val raw = format(value)
        // Don't apply separators to engineering notation (contains 'E')
        return if (raw.contains('E') || raw.contains('e')) {
            raw
        } else {
            addThousandsSeparator(raw)
        }
    }

    fun needsCompactDisplay(value: BigDecimal): Boolean {
        val abs = value.abs()
        return abs >= BigDecimal("1e15") ||
                (abs < BigDecimal("1e-6") && abs.compareTo(BigDecimal.ZERO) != 0)
    }
}
