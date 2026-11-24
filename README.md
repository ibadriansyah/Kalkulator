# Kalkulator — Premium Liquid Glass Android App

A native Android calculator app built with **Jetpack Compose**, featuring a **Liquid Glass** aesthetic design, light/dark mode with live theme switching, and an accurate calculation engine.

---

## 1. ROLE & OBJECTIVE

Built as a **Senior Android Developer**, **Android UI/UX Designer**, **Kotlin Engineer**, and **DevOps Engineer** would deliver it.

**Objective:** A premium calculator app that prioritises design quality, performance, stability, and user experience.

**Requirements:**
- Modern UI/UX with Liquid Glass design system
- Light and Dark modes with live theme switching
- Accurate calculation engine (recursive-descent parser)
- Interactive and smooth animations
- Responsive across various Android screen sizes
- Automated build with GitHub Actions CI/CD
- APK downloadable via GitHub Actions Artifacts and GitHub Releases

---

## 2. TECHNOLOGY STACK

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Design System | Material 3 (customised for Liquid Glass) |
| Build System | Gradle (Kotlin DSL) |
| Android SDK | 8.0 (API 26) min, 35 target |
| Architecture | MVVM (ViewModel + StateFlow) |
| State Management | ViewModel + StateFlow |
| Dependency Injection | Hilt |
| Preferences | Jetpack DataStore |
| CI/CD | GitHub Actions |
| Version Control | Git |

---

## 3. APPLICATION IDENTITY

- **App name:** Kalkulator
- **Application ID:** `com.dabskutz.kalkulator`
- **App label:** Kalkulator
- **Default orientation:** Portrait
- **Custom launcher icon:** Modern calculator symbol, rounded shape, purple/orange blend, glossy/translucent effect, adaptive to Android Adaptive Icon.

---

## 4. DESIGN SYSTEM — LIQUID GLASS AESTHETIC

**Liquid Glass** is implemented as the app's primary identity. It is NOT plain transparency or plain Material Design.

### Visual Characteristics
- Translucent glass surfaces
- Frosted glass effects (blur + translucent layers)
- Glossy highlights (linear gradient overlays)
- Soft ambient lighting (shadows + highlights)
- Subtle reflections (translucent gradient overlays)
- Layered glass components
- Smooth gradients (glow, highlight, shadow)
- Rounded corners (CutCornerShape)
- Soft depth and elevation
- Subtle background blur
- Premium glassmorphism

### Color Palette
| Role | Hex | Usage |
|---|---|---|
| Primary Purple | `#7C3AED` | Primary brand colour |
| Deep Purple | `#4C1D95` | Depth / background |
| Electric Violet | `#A855F7` | Secondary accent |
| Warm Orange | `#FF8A3D` | Accent / highlights |
| Sunset Orange | `#FF6B35` | Secondary accent |
| Soft White | `#F8F7FF` | Display text background |
| Dark Background | `#100D1C` | Dark mode background |
| Dark Surface | `#1C1630` | Dark mode surface |
| Light Background | `#F5F2FF` | Light mode background |
| Light Surface | `#FFFFFF` | Light mode surface |

### Glass Components (Calculator Button)
Every calculator button must have:
- Frosted glass effect (blur + translucent background)
- Translucent background (alpha < 255)
- Subtle glossy reflection (gradient overlay)
- Rounded corners
- Soft border
- Ambient shadow (offset + blur)
- Press animation (scale + brightness)
- Ripple or interaction feedback

### Fallbacks
For devices that do not support blur well, translucent gradients and highlights are drawn as fallbacks so the UI still looks premium.

---

## 5. APPLICATION LAYOUT

### A. Main Screen
**Top section:**
- App header (logo + title)
- Theme settings button
- Calculation history button

**Display screen (glass panel):**
- Previous expression history
- Math expression currently being entered
- Real-time calculation result (larger font)
- Expression uses secondary, low-contrast, flat, dull-colour text per spec

### B. Keypad
4-column grid:
| AC | ± | % | ÷ |
| 7 | 8 | 9 | × |
| 4 | 5 | 6 | − |
| 1 | 2 | 3 | + |
| 0 (span 2) | . | ⌫ | = |

---

## 6. ARCHITECTURE

```
app/
├── Di/
│   └── AppDependencyModule.kt          # Hilt module (DataStoreManager)
├── core/calculator/
│   ├── CalculatorEngine.kt             # Pure calculation engine (BigDecimal)
│   ├── ExpressionParser.kt             # Recursive-descent parser
│   └── NumberFormatter.kt              # Display formatter
├── data/history/
│   ├── HistoryEntry.kt                 # Domain model
│   ├── SerializableHistoryEntry.kt     # JSON-serializable wrapper
│   └── HistoryRepository.kt            # History persistence
├── data/preferences/
│   ├── ThemeMode.kt                    # System / Light / Dark
│   ├── DataStoreManager.kt             # DataStore singleton
│   └── SharedPreferencesManager.kt     # (legacy preference helper)
├── di/
│   └── AppDependencyModule.kt          # Hilt module
├── ui/calculator/
│   ├── CalculatorScreen.kt             # Main screen (display + keypad)
│   ├── CalculatorViewModel.kt          # Presentation logic
│   └── CalculatorState.kt              # UI state
├── ui/components/
│   ├── GlassEffect.kt                  # Liquid glass composable
│   └── CalculatorButton.kt             # Frosted glass button
├── ui/history/
│   └── HistoryScreen.kt                # History list screen
├── ui/settings/
│   └── SettingsScreen.kt               # Theme settings screen
├── ui/theme/
│   ├── Color.kt                        # LiquidGlassColor palette
│   ├── Type.kt                         # LiquidGlassTypography
│   ├── Theme.kt                        # LiquidGlassTheme
│   └── ThemeMode.kt
├── MainActivity.kt
└── CalculatorApplication.kt
```

**MVVM flow:**
```
UI (Composable) ──(state)──> ViewModel (StateFlow)
UI (events)   ←──(actions)── ViewModel
ViewModel ───(repo calls)──> Repository ──> DataStore
                                   │
                                   └──> Engine (BigDecimal)
```

---

## 7. CALCULATION ENGINE

- Pure Kotlin, no dynamic code execution
- **BigDecimal** for arbitrary-precision decimal arithmetic
- Recursive-descent parser for operator precedence
- Precedence: parentheses → unary minus / percent → × / ÷ → + / −
- Division by zero → `Result.failure`
- Malformed expressions → `Result.failure`

---

## 8. TESTING

**Unit tests (`app/src/test/java/.../CalcEngineTest.kt`):**
- Valid expression evaluation (e.g. `2 + 3 * 4 = 14`)
- Division by zero
- Malformed expression

Run: `./gradlew testDebugUnitTest`

---

## 9. CI/CD — GitHub Actions

### Workflows
| File | Trigger | Purpose |
|---|---|---|
| `.github/workflows/android-ci.yml` | `push`, `pull_request` | Lint + build debug/release + upload APKs as artifacts + run unit tests |
| `.github/workflows/android-release.yml` | `push` with tag `v*` | Build release APK + create GitHub Release + upload APK asset |

### Artifacts
- **CI:** Debug APK (`app-debug.apk`) and Release APK (`app-release.apk`) uploaded as artifacts (retention 30 days).
- **Release:** Release APK uploaded as a GitHub Release asset (retention 90 days) and as the `app-release.apk`.

---

## 10. GETTING STARTED

### Prerequisites
- JDK 17+
- Gradle 8.10.2 (via wrapper)
- Android SDK 35 (platforms; build-tools 35.0.0)
- Android Studio or Termux + Android SDK

### Setup
```bash
# Clone the repository
git clone https://github.com/dabskutz/kalkulator.git
cd kalkulator

# Make the wrapper executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew testDebugUnitTest
```

### Termux (Android)
```bash
# Install prerequisites
pkg install openjdk-17
pkg install gradle

# Set environment
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Run the wrapper
./gradlew assembleDebug
```

---

## 11. GENERATOR & TOOLS

- `generate_icons.py` — Pure-Python (stdlib only) script that generates adaptive launcher icons for all density buckets without requiring Pillow.

---

## 12. FUTURE ENHANCEMENTS

- Compose Multiplatform support
- Widget support (AppWidget)
- Results memory / persistent display history
- Expression history with date grouping
- Haptic feedback
- Custom theme presets
- GPU-accelerated rendering polish

---

# Kalkulator
