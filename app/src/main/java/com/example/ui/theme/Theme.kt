package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PizzaPrimary,
    onPrimary = PizzaOnPrimary,
    primaryContainer = PizzaPrimaryContainer,
    onPrimaryContainer = PizzaOnPrimaryContainer,
    secondary = PizzaSecondary,
    onSecondary = PizzaOnSecondary,
    secondaryContainer = PizzaSecondaryContainer,
    onSecondaryContainer = PizzaOnSecondaryContainer,
    tertiary = PizzaTertiary,
    onTertiary = PizzaOnTertiary,
    tertiaryContainer = PizzaTertiaryContainer,
    onTertiaryContainer = PizzaOnTertiaryContainer,
    background = PizzaBackground,
    onBackground = PizzaOnBackground,
    surface = PizzaSurface,
    onSurface = PizzaOnSurface,
    surfaceVariant = PizzaSurfaceVariant,
    onSurfaceVariant = PizzaOnSurfaceVariant,
    outline = PizzaOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4A8),
    onPrimary = Color(0xFF690003),
    primaryContainer = Color(0xFF930007),
    onPrimaryContainer = Color(0xFFFFDAD4),
    secondary = Color(0xFFFFB59B),
    onSecondary = Color(0xFF5F1500),
    secondaryContainer = Color(0xFF832100),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = Color(0xFF96D799),
    onTertiary = Color(0xFF00390E),
    tertiaryContainer = Color(0xFF13521E),
    onTertiaryContainer = Color(0xFFC8E6C9),
    background = Color(0xFF201A19),
    onBackground = Color(0xFFEDE0DD),
    surface = Color(0xFF201A19),
    onSurface = Color(0xFFEDE0DD),
    surfaceVariant = Color(0xFF53433F),
    onSurfaceVariant = Color(0xFFD8C2BC),
    outline = Color(0xFFA08C87)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
