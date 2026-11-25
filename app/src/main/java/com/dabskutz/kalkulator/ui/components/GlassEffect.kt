package com.dabskutz.kalkulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dabskutz.kalkulator.ui.theme.LocalDarkTheme

/**
 * Creates an authentic Liquid Glass effect modifier.
 *
 * Visual Layers:
 * 1. Physical drop shadow for spatial elevation
 * 2. Translucent frosted glass body gradient
 * 3. Upper-half convex specular gloss reflection
 * 4. Crisp top-lip bevel highlight (physical glass edge)
 * 5. Diagonal light refraction sheen
 * 6. Multi-stop refraction border (light source at top-left)
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 6.dp,
    highlightIntensity: Float = 0.38f,
    isDark: Boolean = true
): Modifier {
    val baseGlassColor = if (backgroundColor != Color.Unspecified) {
        backgroundColor
    } else if (isDark) {
        Color(0x28FFFFFFL) // Translucent crystal glass over black
    } else {
        Color(0x85FFFFFFL) // Frosted pearl glass
    }

    val rimHighlightColor = if (borderColor != Color.Unspecified) {
        borderColor
    } else if (isDark) {
        Color.White.copy(alpha = 0.65f)
    } else {
        Color.White.copy(alpha = 0.90f)
    }

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            rimHighlightColor,
            rimHighlightColor.copy(alpha = (rimHighlightColor.alpha * 0.35f).coerceIn(0f, 1f)),
            rimHighlightColor.copy(alpha = (rimHighlightColor.alpha * 0.08f).coerceIn(0f, 1f)),
            rimHighlightColor.copy(alpha = (rimHighlightColor.alpha * 0.45f).coerceIn(0f, 1f))
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            spotColor = if (isDark) Color.Black.copy(alpha = 0.85f) else Color(0x337C3AEDL),
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.55f) else Color(0x1A7C3AEDL)
        )
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    baseGlassColor.copy(alpha = (baseGlassColor.alpha * 1.35f).coerceAtMost(1f)),
                    baseGlassColor.copy(alpha = (baseGlassColor.alpha * 0.65f)),
                    baseGlassColor.copy(alpha = (baseGlassColor.alpha * 1.15f).coerceAtMost(1f))
                )
            ),
            shape = shape
        )
        .drawBehind {
            // 1. Upper-half convex specular gloss reflection
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = highlightIntensity),
                        Color.White.copy(alpha = highlightIntensity * 0.20f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.55f
                ),
                cornerRadius = CornerRadius(size.width * 0.25f, size.width * 0.25f)
            )

            // 2. Diagonal sheen across upper quadrant
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = highlightIntensity * 0.45f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.65f, size.height * 0.65f)
                ),
                cornerRadius = CornerRadius(size.width * 0.25f, size.width * 0.25f)
            )

            // 3. Beveled top specular lip (creates realistic thickness of polished glass)
            drawLine(
                color = Color.White.copy(alpha = (highlightIntensity * 1.6f).coerceAtMost(0.95f)),
                start = Offset(size.width * 0.15f, 2f),
                end = Offset(size.width * 0.85f, 2f),
                strokeWidth = 2.5f
            )
        }
        .border(
            width = 1.3.dp,
            brush = borderBrush,
            shape = shape
        )
}

/**
 * Liquid Glass container Composable.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    elevation: Dp = 6.dp,
    highlightIntensity: Float = 0.38f,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalDarkTheme.current
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
