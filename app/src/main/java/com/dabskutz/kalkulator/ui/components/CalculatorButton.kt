package com.dabskutz.kalkulator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dabskutz.kalkulator.ui.theme.LiquidGlassColor
import com.dabskutz.kalkulator.ui.theme.LocalDarkTheme

enum class ButtonStyle {
    NUMBER,
    OPERATOR,
    EQUALS,
    ACTION
}

/**
 * A calculator button adhering strictly to the Liquid Glass visual language.
 * Featuring frosted translucent glass tiles, specular highlights, and refraction borders.
 */
@Composable
fun CalculatorButton(
    text: String,
    style: ButtonStyle = ButtonStyle.NUMBER,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    shape: Shape = RoundedCornerShape(22.dp),
    onClick: () -> Unit
) {
    val isDark = LocalDarkTheme.current
    val haptic = LocalHapticFeedback.current

    val (glassBg, glassBorder, textColor, highlightIntensity, elevation) = when (style) {
        ButtonStyle.NUMBER -> {
            if (isDark) {
                ButtonGlassConfig(
                    bg = Color(0x33FFFFFFL), // 20% white translucent crystal glass
                    border = Color.White.copy(alpha = 0.65f), // Bright specular refraction rim
                    text = LiquidGlassColor.DarkTextPrimary,
                    highlight = 0.40f,
                    elevation = 6.dp
                )
            } else {
                ButtonGlassConfig(
                    bg = Color(0x8AFFFFFFL), // 54% frosted pearl glass
                    border = Color.White.copy(alpha = 0.90f),
                    text = LiquidGlassColor.LightTextPrimary,
                    highlight = 0.45f,
                    elevation = 4.dp
                )
            }
        }
        ButtonStyle.OPERATOR -> {
            ButtonGlassConfig(
                bg = if (isDark) LiquidGlassColor.DarkAccent.copy(alpha = 0.42f) else LiquidGlassColor.LightAccent.copy(alpha = 0.35f),
                border = if (isDark) LiquidGlassColor.DarkAccentLight.copy(alpha = 0.75f) else LiquidGlassColor.LightAccent.copy(alpha = 0.70f),
                text = if (isDark) LiquidGlassColor.DarkOrangeLight else LiquidGlassColor.DarkAccent,
                highlight = 0.38f,
                elevation = 6.dp
            )
        }
        ButtonStyle.EQUALS -> {
            ButtonGlassConfig(
                bg = if (isDark) LiquidGlassColor.DarkOrange.copy(alpha = 0.70f) else LiquidGlassColor.LightOrange.copy(alpha = 0.75f),
                border = Color.White.copy(alpha = 0.85f),
                text = Color.White,
                highlight = 0.50f,
                elevation = 8.dp
            )
        }
        ButtonStyle.ACTION -> {
            ButtonGlassConfig(
                bg = if (isDark) Color(0x40EF4444L) else Color(0x30EF4444L),
                border = LiquidGlassColor.ActionClear.copy(alpha = 0.75f),
                text = if (isDark) Color(0xFFFF9494L) else LiquidGlassColor.ActionClear,
                highlight = 0.35f,
                elevation = 5.dp
            )
        }
    }

    Box(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                backgroundColor = glassBg,
                borderColor = glassBorder,
                elevation = elevation,
                highlightIntensity = highlightIntensity,
                isDark = isDark
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = textColor),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (style == ButtonStyle.NUMBER) FontWeight.Normal else FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

private data class ButtonGlassConfig(
    val bg: Color,
    val border: Color,
    val text: Color,
    val highlight: Float,
    val elevation: Dp
)

/**
 * Reusable Liquid Glass card container.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalDarkTheme.current
    Box(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                elevation = elevation,
                isDark = isDark
            )
            .padding(16.dp),
        content = content
    )
}
