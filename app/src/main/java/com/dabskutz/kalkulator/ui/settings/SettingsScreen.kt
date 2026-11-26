package com.dabskutz.kalkulator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dabskutz.kalkulator.data.preferences.ThemeMode
import com.dabskutz.kalkulator.ui.components.GlassBox
import com.dabskutz.kalkulator.ui.components.LiquidGlassCard
import com.dabskutz.kalkulator.ui.theme.LiquidGlassColor
import com.dabskutz.kalkulator.ui.theme.LocalDarkTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    themeMode: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    onClearHistory: () -> Unit
) {
    val isDark = LocalDarkTheme.current

    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF000000),
                Color(0xFF040406),
                Color(0xFF000000)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF3EDFA),
                Color(0xFFE8DEF8),
                Color(0xFFF7F3FC)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassBox(
                    shape = RoundedCornerShape(14.dp),
                    elevation = 2.dp,
                    modifier = Modifier.size(42.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Appearance & Theme
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = if (isDark) LiquidGlassColor.DarkOrange else LiquidGlassColor.LightOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Appearance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeOptionRow(
                        title = "System Default",
                        subtitle = "Match device dark/light theme setting",
                        isSelected = themeMode == ThemeMode.System,
                        onSelect = { onThemeChanged(ThemeMode.System) },
                        isDark = isDark
                    )
                    ThemeOptionRow(
                        title = "Light Mode",
                        subtitle = "Frosted pearl glass aesthetics",
                        isSelected = themeMode == ThemeMode.Light,
                        onSelect = { onThemeChanged(ThemeMode.Light) },
                        isDark = isDark
                    )
                    ThemeOptionRow(
                        title = "Dark Mode",
                        subtitle = "Pure black liquid glass aesthetics",
                        isSelected = themeMode == ThemeMode.Dark,
                        onSelect = { onThemeChanged(ThemeMode.Dark) },
                        isDark = isDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Data & Privacy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = LiquidGlassColor.ActionClear,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Data Management",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClearHistory),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Clear History",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = LiquidGlassColor.ActionClear
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Erase all saved calculations",
                            fontSize = 13.sp,
                            color = if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear",
                        tint = LiquidGlassColor.ActionClear
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: About
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isDark) LiquidGlassColor.DarkAccentLight else LiquidGlassColor.LightAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "About",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Kalkulator v1.0.2",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Liquid Glass Edition — arbitrary precision math engine built with Jetpack Compose & Material 3.",
                        fontSize = 13.sp,
                        color = if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) {
                    if (isDark) LiquidGlassColor.DarkOrangeLight else LiquidGlassColor.DarkAccent
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isDark) LiquidGlassColor.DarkOrange else LiquidGlassColor.LightAccent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
