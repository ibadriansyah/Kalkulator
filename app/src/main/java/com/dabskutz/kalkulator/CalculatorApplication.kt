package com.dabskutz.kalkulator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Kalkulator app.
 *
 * Initialised by Hilt. The application-level context is made available to
 * the dependency graph (Hilt/SingletonComponent).
 */
@HiltAndroidApp
class CalculatorApplication : Application()
