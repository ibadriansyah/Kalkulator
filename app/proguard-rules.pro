# Keep only the rules you need. For this calculator app:
# - Do NOT shrink or obfuscate code (this is a demo app with no sensitive logic)
# - Keep the package names so the app remains debuggable

# Don't delete any classes
-keep class com.dabskutz.kalkulator.** { *; }

# Keep Math-related classes intact
-keep class kotlin.math.** { *; }

# Retain synthetic accessors (Compose internal)
-keepclassmembers class * {
    @org.jetbrains.annotations.NotNull <fields>;
}
