package com.dabskutz.kalkulator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Liquid Glass typography scale for the calculator. Clean, legible, premium. */
object LiquidGlassTypography {

    /** Display scale for the main result — intentionally large and clear. */
    val display = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.W500,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        color = LiquidGlassColor.LightTextPrimary
    )

    /** Expression being typed — smaller, secondary tone, monospace. */
    val expression = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        color = LiquidGlassColor.LightTextSecondary
    )

    /** History item text. */
    val history = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = LiquidGlassColor.LightTextSecondary
    )

    /** Button label text. */
    val button = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = LiquidGlassColor.LightTextPrimary
    )

    /** Settings / navigation labels. */
    val label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = LiquidGlassColor.LightTextSecondary
    )

    /** Headers. */
    val headline = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W700,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = LiquidGlassColor.LightTextPrimary
    )

    /** Base Material3 typography with custom calculator styles. */
    val base = Typography(
        displayLarge = display,
        displayMedium = display,
        displaySmall = display,
        headlineLarge = headline,
        headlineMedium = headline,
        headlineSmall = headline,
        titleLarge = label,
        titleMedium = label,
        titleSmall = label,
        bodyLarge = label,
        bodyMedium = label,
        bodySmall = label,
        labelLarge = label,
        labelMedium = label,
        labelSmall = label
    )
}
