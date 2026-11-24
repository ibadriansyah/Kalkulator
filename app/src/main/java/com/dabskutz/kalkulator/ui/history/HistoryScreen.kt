package com.dabskutz.kalkulator.ui.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dabskutz.kalkulator.core.calculator.CalculatorEngine
import com.dabskutz.kalkulator.data.history.HistoryEntry
import com.dabskutz.kalkulator.ui.components.GlassBox
import com.dabskutz.kalkulator.ui.components.LiquidGlassCard
import com.dabskutz.kalkulator.ui.theme.LiquidGlassColor

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onReuseEntry: (HistoryEntry) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onClearAll: () -> Unit,
    historyEntries: List<HistoryEntry>
) {
    val isDark = isSystemInDarkTheme()

    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F0B1E),
                Color(0xFF160E2E),
                Color(0xFF0B0716)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "History",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (historyEntries.isNotEmpty()) {
                    GlassBox(
                        shape = RoundedCornerShape(14.dp),
                        elevation = 2.dp,
                        modifier = Modifier.size(42.dp)
                    ) {
                        IconButton(
                            onClick = onClearAll,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear All",
                                tint = LiquidGlassColor.ActionClear
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No calculations yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your past calculation history will be displayed here.",
                                fontSize = 14.sp,
                                color = if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = historyEntries,
                        key = { it.id }
                    ) { entry ->
                        HistoryCard(
                            entry = entry,
                            onReuse = { onReuseEntry(entry) },
                            onDelete = { onDeleteEntry(entry.id) },
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    entry: HistoryEntry,
    onReuse: () -> Unit,
    onDelete: () -> Unit,
    isDark: Boolean
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReuse),
        elevation = 3.dp,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.expression,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "= " + CalculatorEngine.formatResult(entry.result),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDark) LiquidGlassColor.DarkOrangeLight else LiquidGlassColor.DarkAccent,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete entry",
                    tint = LiquidGlassColor.ActionClear.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
