package com.dabskutz.kalkulator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

enum class ButtonStyle {
    NUMBER,
    OPERATOR,
    EQUALS,
    ACTION
}

/**
 * A calculator button adhering to the Liquid Glass visual language.
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
    val isDark = isSystemInDarkTheme()
    val haptic = LocalHapticFeedback.current

    val (glassBg, glassBorder, textColor) = when (style) {
        ButtonStyle.NUMBER -> {
            if (isDark) {
                Triple(
                    Color(0x2EFFFFFFL),
                    Color(0x3DFFFFFFL),
                    LiquidGlassColor.DarkTextPrimary
                )
            } else {
                Triple(
                    Color(0x73FFFFFFL),
                    Color(0x8CFFFFFFL),
                    LiquidGlassColor.LightTextPrimary
                )
            }
        }
        ButtonStyle.OPERATOR -> {
            Triple(
                LiquidGlassColor.ActionOperator.copy(alpha = if (isDark) 0.35f else 0.40f),
                LiquidGlassColor.ActionOperator.copy(alpha = 0.70f),
                if (isDark) LiquidGlassColor.DarkOrangeLight else LiquidGlassColor.DarkAccent
            )
        }
        ButtonStyle.EQUALS -> {
            Triple(
                LiquidGlassColor.ActionEquals.copy(alpha = if (isDark) 0.55f else 0.65f),
                LiquidGlassColor.ActionEquals.copy(alpha = 0.85f),
                Color.White
            )
        }
        ButtonStyle.ACTION -> {
            Triple(
                LiquidGlassColor.ActionClear.copy(alpha = if (isDark) 0.28f else 0.32f),
                LiquidGlassColor.ActionClear.copy(alpha = 0.60f),
                if (isDark) Color(0xFFFCA5A5L) else LiquidGlassColor.ActionClear
            )
        }
    }

    Box(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                backgroundColor = glassBg,
                borderColor = glassBorder,
                elevation = if (style == ButtonStyle.EQUALS) 8.dp else 4.dp,
                highlightIntensity = if (style == ButtonStyle.EQUALS) 0.4f else 0.25f,
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
    val isDark = isSystemInDarkTheme()
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
