package com.dabskutz.kalkulator.core.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionParserTest {

    @Test
    fun testValidParsing() {
        val parser = ExpressionParser("12 + 34 × 2")
        val result = parser.parse()
        assertTrue(result.isSuccess)
        assertEquals("80", CalculatorEngine.formatResult(result.getOrThrow()))
    }

    @Test
    fun testMismatchedParentheses() {
        val parser = ExpressionParser("(12 + 34")
        val result = parser.parse()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CalculatorEngine.InvalidExpressionException)
    }

    @Test
    fun testInvalidCharacters() {
        val parser = ExpressionParser("12 + abc")
        val result = parser.parse()
        assertTrue(result.isFailure)
    }

    @Test
    fun testComplexExpression() {
        val parser = ExpressionParser("((10 + 20) ÷ 2) × (5 − 3)")
        val result = parser.parse()
        assertTrue(result.isSuccess)
        assertEquals("30", CalculatorEngine.formatResult(result.getOrThrow()))
    }
}
