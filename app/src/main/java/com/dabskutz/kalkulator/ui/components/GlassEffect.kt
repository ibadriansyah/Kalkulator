package com.dabskutz.kalkulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dabskutz.kalkulator.ui.theme.LiquidGlassColor

/**
 * Creates a premium Liquid Glass effect modifier.
 *
 * Layers:
 * 1. Soft ambient shadow for depth
 * 2. Translucent frosted glass gradient
 * 3. Specular glossy highlight along top-left edge
 * 4. Crisp subtle border representing glass refraction
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 6.dp,
    highlightIntensity: Float = 0.25f,
    isDark: Boolean = true
): Modifier {
    val baseGlassColor = if (backgroundColor != Color.Unspecified) {
        backgroundColor
    } else if (isDark) {
        Color(0x2AFFFFFFL) // 16% white overlay
    } else {
        Color(0x66FFFFFFL) // 40% white overlay
    }

    val glassBorderColor = if (borderColor != Color.Unspecified) {
        borderColor
    } else if (isDark) {
        Color(0x3DFFFFFFL) // 24% white highlight
    } else {
        Color(0x80FFFFFFL) // 50% white highlight
    }

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            spotColor = if (isDark) Color.Black.copy(alpha = 0.35f) else Color(0x337C3AEDL),
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.15f) else Color(0x1A7C3AEDL)
        )
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    baseGlassColor.copy(alpha = (baseGlassColor.alpha * 1.25f).coerceAtMost(1f)),
                    baseGlassColor.copy(alpha = (baseGlassColor.alpha * 0.75f))
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            ),
            shape = shape
        )
        .drawBehind {
            // Specular reflection / glossy highlight from top-left
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = highlightIntensity),
                        Color.White.copy(alpha = highlightIntensity * 0.2f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.7f, size.height * 0.7f)
                )
            )
        }
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    glassBorderColor,
                    glassBorderColor.copy(alpha = glassBorderColor.alpha * 0.3f),
                    glassBorderColor.copy(alpha = glassBorderColor.alpha * 0.6f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            ),
            shape = shape
        )
}

/**
 * Liquid Glass container Composable.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 6.dp,
    highlightIntensity: Float = 0.25f,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = modifier.liquidGlass(
            shape = shape,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            elevation = elevation,
            highlightIntensity = highlightIntensity,
            isDark = isDark
        ),
        content = content
    )
}
