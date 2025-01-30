package com.example.smsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary      = darkPrimary,
    onPrimary    = darkOnPrimary,

    secondary    = darkSecondary,
    onSecondary  = darkOnSecondary,

    tertiary     = Color(0xFFFF0000),
    onTertiary   = Color(0xFFAC378F),

    background   = darkBackground,
    onBackground = darkOnBackground,

    surface      = darkSurface,
    onSurface    = darkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary      = lightPrimary,
    onPrimary    = lightOnPrimary,

    secondary    = lightSecondary,
    onSecondary  = lightOnSecondary,

    tertiary     = Pink40,
    onTertiary   = Color(0xFFAC378F),

    background   = lightBackground,
    onBackground = lightOnBackground,

    surface      = lightSurface,
    onSurface    = lightOnSurface
)

@Composable
fun SmsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit) {

    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
