package com.dabskutz.kalkulator.core.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class CalculatorEngineTest {

    @Test
    fun testBasicAddition() {
        val result = CalculatorEngine.evaluate("2 + 3")
        assertTrue(result.isSuccess)
        assertEquals("5", CalculatorEngine.formatResult(result.getOrThrow()))
    }

    @Test
    fun testBasicSubtraction() {
        val result = CalculatorEngine.evaluate("10 − 4")
        assertTrue(result.isSuccess)
        assertEquals("6", CalculatorEngine.formatResult(result.getOrThrow()))

        val resultAscii = CalculatorEngine.evaluate("10 - 4")
        assertTrue(resultAscii.isSuccess)
        assertEquals("6", CalculatorEngine.formatResult(resultAscii.getOrThrow()))
    }

    @Test
    fun testBasicMultiplication() {
        val result = CalculatorEngine.evaluate("6 × 7")
        assertTrue(result.isSuccess)
        assertEquals("42", CalculatorEngine.formatResult(result.getOrThrow()))

        val resultAscii = CalculatorEngine.evaluate("6 * 7")
        assertTrue(resultAscii.isSuccess)
        assertEquals("42", CalculatorEngine.formatResult(resultAscii.getOrThrow()))
    }

    @Test
    fun testBasicDivision() {
        val result = CalculatorEngine.evaluate("20 ÷ 4")
        assertTrue(result.isSuccess)
        assertEquals("5", CalculatorEngine.formatResult(result.getOrThrow()))

        val resultAscii = CalculatorEngine.evaluate("20 / 4")
        assertTrue(resultAscii.isSuccess)
        assertEquals("5", CalculatorEngine.formatResult(resultAscii.getOrThrow()))
    }

    @Test
    fun testDivisionByZero() {
        val result = CalculatorEngine.evaluate("42 ÷ 0")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CalculatorEngine.DivisionByZeroException)
    }

    @Test
    fun testOperatorPrecedence() {
        // 2 + 3 * 4 should be 14, not 20
        val result = CalculatorEngine.evaluate("2 + 3 × 4")
        assertTrue(result.isSuccess)
        assertEquals("14", CalculatorEngine.formatResult(result.getOrThrow()))

        // 10 - 6 / 2 should be 7, not 2
        val result2 = CalculatorEngine.evaluate("10 − 6 ÷ 2")
        assertTrue(result2.isSuccess)
        assertEquals("7", CalculatorEngine.formatResult(result2.getOrThrow()))
    }

    @Test
    fun testParentheses() {
        val result = CalculatorEngine.evaluate("(2 + 3) × 4")
        assertTrue(result.isSuccess)
        assertEquals("20", CalculatorEngine.formatResult(result.getOrThrow()))

        val nested = CalculatorEngine.evaluate("2 × (3 + (4 × 5))")
        assertTrue(nested.isSuccess)
        assertEquals("46", CalculatorEngine.formatResult(nested.getOrThrow()))
    }

    @Test
    fun testNegativeNumbers() {
        val result = CalculatorEngine.evaluate("−5 + 8")
        assertTrue(result.isSuccess)
        assertEquals("3", CalculatorEngine.formatResult(result.getOrThrow()))

        val result2 = CalculatorEngine.evaluate("10 × −2")
        assertTrue(result2.isSuccess)
        assertEquals("-20", CalculatorEngine.formatResult(result2.getOrThrow()))
    }

    @Test
    fun testDecimalCalculations() {
        val result = CalculatorEngine.evaluate("0.1 + 0.2")
        assertTrue(result.isSuccess)
        // With BigDecimal this is exact 0.3, unlike standard IEEE float!
        assertEquals("0.3", CalculatorEngine.formatResult(result.getOrThrow()))

        val result2 = CalculatorEngine.evaluate("1.25 × 4")
        assertTrue(result2.isSuccess)
        assertEquals("5", CalculatorEngine.formatResult(result2.getOrThrow()))
    }

    @Test
    fun testPercentage() {
        val result = CalculatorEngine.evaluate("50%")
        assertTrue(result.isSuccess)
        assertEquals("0.5", CalculatorEngine.formatResult(result.getOrThrow()))

        val result2 = CalculatorEngine.evaluate("200 × 15%")
        assertTrue(result2.isSuccess)
        assertEquals("30", CalculatorEngine.formatResult(result2.getOrThrow()))
    }

    @Test
    fun testEmptyExpression() {
        val result = CalculatorEngine.evaluate("")
        assertTrue(result.isSuccess)
        assertEquals("0", CalculatorEngine.formatResult(result.getOrThrow()))
    }
}
