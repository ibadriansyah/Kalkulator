package com.dabskutz.kalkulator.core.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class NumberFormatterTest {

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
}
