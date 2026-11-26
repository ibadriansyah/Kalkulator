package com.dabskutz.kalkulator.core.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class NumberFormatterTest {

    // ---- Basic format (BigDecimal -> plain string, no separators) ----

    @Test
    fun testZeroFormatting() {
        assertEquals("0", NumberFormatter.format(BigDecimal.ZERO))
        assertEquals("0", NumberFormatter.format(BigDecimal("0.0000")))
    }

    @Test
    fun testIntegerFormatting() {
        assertEquals("123", NumberFormatter.format(BigDecimal("123")))
        assertEquals("1000", NumberFormatter.format(BigDecimal("1000.00")))
    }

    @Test
    fun testDecimalFormatting() {
        assertEquals("3.14159", NumberFormatter.format(BigDecimal("3.14159")))
        assertEquals("0.5", NumberFormatter.format(BigDecimal("0.50000")))
    }

    @Test
    fun testTrailingZerosStripped() {
        assertEquals("42", NumberFormatter.format(BigDecimal("42.000000")))
        assertEquals("42.1", NumberFormatter.format(BigDecimal("42.100000")))
    }

    // ---- addThousandsSeparator ----

    @Test
    fun testThousandsSeparator_1000() {
        assertEquals("1.000", NumberFormatter.addThousandsSeparator("1000"))
    }

    @Test
    fun testThousandsSeparator_100000() {
        assertEquals("100.000", NumberFormatter.addThousandsSeparator("100000"))
    }

    @Test
    fun testThousandsSeparator_1000000() {
        assertEquals("1.000.000", NumberFormatter.addThousandsSeparator("1000000"))
    }

    @Test
    fun testThousandsSeparator_100000000() {
        assertEquals("100.000.000", NumberFormatter.addThousandsSeparator("100000000"))
    }

    @Test
    fun testThousandsSeparator_withDecimal() {
        // "123456.78" -> "123.456,78" (id-ID locale: dot=thousands, comma=decimal)
        assertEquals("123.456,78", NumberFormatter.addThousandsSeparator("123456.78"))
    }

    @Test
    fun testThousandsSeparator_negative() {
        assertEquals("−1.000.000", NumberFormatter.addThousandsSeparator("-1000000"))
    }

    @Test
    fun testThousandsSeparator_smallNumber() {
        assertEquals("999", NumberFormatter.addThousandsSeparator("999"))
        assertEquals("42", NumberFormatter.addThousandsSeparator("42"))
        assertEquals("0", NumberFormatter.addThousandsSeparator("0"))
    }

    // ---- formatExpression ----

    @Test
    fun testFormatExpression_singleNumber() {
        assertEquals("100.000", NumberFormatter.formatExpression("100000"))
        assertEquals("1.000.000", NumberFormatter.formatExpression("1000000"))
    }

    @Test
    fun testFormatExpression_withOperators() {
        assertEquals("100.000+2.500.000", NumberFormatter.formatExpression("100000+2500000"))
        assertEquals("1.000×999", NumberFormatter.formatExpression("1000×999"))
    }

    @Test
    fun testFormatExpression_complexExpression() {
        assertEquals("(100.000+50.000)×2", NumberFormatter.formatExpression("(100000+50000)×2"))
    }

    @Test
    fun testFormatExpression_withDecimalNumbers() {
        assertEquals("1.234.567,89+100", NumberFormatter.formatExpression("1234567.89+100"))
    }

    // ---- stripSeparators ----

    @Test
    fun testStripSeparators_basic() {
        assertEquals("1000000", NumberFormatter.stripSeparators("1.000.000"))
    }

    @Test
    fun testStripSeparators_withDecimal() {
        assertEquals("123456.78", NumberFormatter.stripSeparators("123.456,78"))
    }

    @Test
    fun testStripSeparators_noSeparators() {
        assertEquals("42", NumberFormatter.stripSeparators("42"))
    }

    // ---- formatResultDisplay ----

    @Test
    fun testFormatResultDisplay_thousands() {
        assertEquals("1.000", NumberFormatter.formatResultDisplay(BigDecimal("1000")))
        assertEquals("100.000", NumberFormatter.formatResultDisplay(BigDecimal("100000")))
        assertEquals("1.000.000", NumberFormatter.formatResultDisplay(BigDecimal("1000000")))
        assertEquals("100.000.000", NumberFormatter.formatResultDisplay(BigDecimal("100000000")))
    }

    @Test
    fun testFormatResultDisplay_withDecimals() {
        assertEquals("1.234.567,5", NumberFormatter.formatResultDisplay(BigDecimal("1234567.5")))
    }

    // ---- Round-trip: format -> strip -> compute ----

    @Test
    fun testRoundTrip_formatThenStrip() {
        val raw = "1000000"
        val formatted = NumberFormatter.addThousandsSeparator(raw)
        assertEquals("1.000.000", formatted)
        val restored = NumberFormatter.stripSeparators(formatted)
        assertEquals(raw, restored)
    }

    @Test
    fun testRoundTrip_expressionWithOperators() {
        val raw = "100000+2500000"
        val formatted = NumberFormatter.formatExpression(raw)
        assertEquals("100.000+2.500.000", formatted)
        // stripSeparators works on the whole expression
        val restored = NumberFormatter.stripSeparators(formatted)
        assertEquals(raw, restored)
    }
}
