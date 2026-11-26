package com.dabskutz.kalkulator.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dabskutz.kalkulator.ui.components.ButtonStyle
import com.dabskutz.kalkulator.ui.components.CalculatorButton
import com.dabskutz.kalkulator.ui.components.GlassBox
import com.dabskutz.kalkulator.ui.theme.LiquidGlassColor
import com.dabskutz.kalkulator.ui.theme.LocalDarkTheme

@Composable
fun CalculatorScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val state by viewModel.state.collectAsState()
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
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kalkulator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassBox(
                        shape = RoundedCornerShape(14.dp),
                        elevation = 2.dp,
                        modifier = Modifier.size(42.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateToHistory,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    GlassBox(
                        shape = RoundedCornerShape(14.dp),
                        elevation = 2.dp,
                        modifier = Modifier.size(42.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Section
            GlassBox(
                shape = RoundedCornerShape(28.dp),
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (state.expression.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onBackspace() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = (if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        val exprScrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(exprScrollState, reverseScrolling = true),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = state.displayExpression.ifEmpty { " " },
                                fontSize = 26.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isDark) LiquidGlassColor.DarkTextSecondary else LiquidGlassColor.LightTextSecondary,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
                        }

                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.error ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LiquidGlassColor.ActionClear,
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val resultScrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(resultScrollState, reverseScrolling = true),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = state.displayResult,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Keypad Grid Section: 5 rows x 4 columns
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: AC, (), %, ÷
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "AC",
                        style = ButtonStyle.ACTION,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onClear() }
                    )
                    CalculatorButton(
                        text = "( )",
                        style = ButtonStyle.OPERATOR,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onParenthesis() }
                    )
                    CalculatorButton(
                        text = "%",
                        style = ButtonStyle.OPERATOR,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onPercent() }
                    )
                    CalculatorButton(
                        text = "÷",
                        style = ButtonStyle.OPERATOR,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onOperator("÷") }
                    )
                }

                // Row 2: 7, 8, 9, ×
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "7",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("7") }
                    )
                    CalculatorButton(
                        text = "8",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("8") }
                    )
                    CalculatorButton(
                        text = "9",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("9") }
                    )
                    CalculatorButton(
                        text = "×",
                        style = ButtonStyle.OPERATOR,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onOperator("×") }
                    )
                }

                // Row 3: 4, 5, 6, −
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "4",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("4") }
                    )
                    CalculatorButton(
                        text = "5",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("5") }
                    )
                    CalculatorButton(
                        text = "6",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("6") }
                    )
                    CalculatorButton(
                        text = "−",
                        style = ButtonStyle.OPERATOR,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onOperator("−") }
                    )
                }

                // Row 4: 1, 2, 3, +
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "1",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("1") }
                    )
                    CalculatorButton(
                        text = "2",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("2") }
                    )
                    CalculatorButton(
                        text = "3",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("3") }
                    )
                    CalculatorButton(
                        text = "+",
                        style = ButtonStyle.OPERATOR,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onOperator("+") }
                    )
                }

                // Row 5: ±, 0, ., =
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "±",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onToggleSign() }
                    )
                    CalculatorButton(
                        text = "0",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDigit("0") }
                    )
                    CalculatorButton(
                        text = ".",
                        style = ButtonStyle.NUMBER,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onDecimal() }
                    )
                    CalculatorButton(
                        text = "=",
                        style = ButtonStyle.EQUALS,
                        modifier = Modifier.weight(1f).height(68.dp),
                        onClick = { viewModel.onEquals() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
