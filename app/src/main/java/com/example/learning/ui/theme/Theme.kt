package com.example.learning.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme // Optional: Define if you want a specific dark theme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Color Scheme using YOUR app's defined colors
private val LightAppColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryTurquoise,
    background = BackgroundLightCyan,
    surface = Color.White, // Standard surface color (e.g., for Cards)
    onPrimary = Color.White, // Text/icons on Primary color
    onSecondary = Color.White, // Text/icons on Secondary color
    onBackground = OnSurfaceVariantDarkGray, // Main text color on BackgroundLightCyan
    onSurface = OnSurfaceVariantDarkGray,   // Main text color on Surface (White)
    surfaceVariant = SurfaceVariantGray,    // Backgrounds for elements like chips, outlined text fields
    onSurfaceVariant = OnSurfaceVariantDarkGray, // Text on surfaceVariant elements
    error = Color(0xFFB00020), // Standard Material error color
    outline = TextMutedGray     // For borders like OutlinedTextField if not focused
)

// Optional: Define a Dark Color Scheme using your app's defined colors, adjusted for dark mode
// This is a basic example; you'd fine-tune these for your dark theme.
private val DarkAppColorScheme = darkColorScheme(
    primary = PrimaryBlue, // Or a slightly lighter/desaturated version for dark theme
    secondary = SecondaryTurquoise, // Or a slightly lighter/desaturated version
    background = Color(0xFF121212), // Common dark theme background
    surface = Color(0xFF1E1E1E),   // Common dark theme surface (cards etc.)
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE0E0E0), // Light gray text on dark background
    onSurface = Color(0xFFE0E0E0),   // Light gray text on dark surface
    surfaceVariant = Color(0xFF303030), // Darker variant for chips etc.
    onSurfaceVariant = Color(0xFFBDBDBD), // Lighter text on dark surfaceVariant
    error = Color(0xFFCF6679), // Standard Material dark error color
    outline = Color(0xFF757575) // Muted border for dark theme
)

@Composable
fun MedicalAppTheme( // Your theme function
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to true if you want to use Material You dynamic colors on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkAppColorScheme // Use your custom DarkAppColorScheme
        else -> LightAppColorScheme     // Use your custom LightAppColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Example: set status bar to primary color
            // Adjust icon color on status bar based on whether the status bar color is light or dark
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // For more precise control, you'd check the luminance of colorScheme.primary
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Ensure AppTypography is defined in Type.kt (or Typography.kt)
        content = content
    )
}