package com.dabskutz.kalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dabskutz.kalkulator.data.preferences.ThemeMode
import com.dabskutz.kalkulator.ui.calculator.CalculatorScreen
import com.dabskutz.kalkulator.ui.calculator.CalculatorViewModel
import com.dabskutz.kalkulator.ui.history.HistoryScreen
import com.dabskutz.kalkulator.ui.settings.SettingsScreen
import com.dabskutz.kalkulator.ui.theme.LiquidGlassTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalkulatorApp()
        }
    }
}

@Composable
fun KalkulatorApp(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val darkTheme = when (state.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    LiquidGlassTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "calculator"
            ) {
                composable("calculator") {
                    CalculatorScreen(
                        onNavigateToSettings = { navController.navigate("settings") },
                        onNavigateToHistory = { navController.navigate("history") },
                        viewModel = viewModel
                    )
                }
                composable("history") {
                    HistoryScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onReuseEntry = { entry ->
                            viewModel.onReuseHistory(entry)
                            navController.popBackStack()
                        },
                        onDeleteEntry = { id ->
                            viewModel.onDeleteHistoryEntry(id)
                        },
                        onClearAll = {
                            viewModel.onClearHistory()
                        },
                        historyEntries = state.history
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        themeMode = state.themeMode,
                        onThemeChanged = { mode -> viewModel.onThemeChanged(mode) },
                        onClearHistory = { viewModel.onClearHistory() }
                    )
                }
            }
        }
    }
}
