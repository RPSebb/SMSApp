package com.example.smsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary      = darkPrimary,
    secondary    = Color(0xFFFF0000),
    tertiary     = Color(0xFFFF0000),
    background   = darkBackground,
    surface      = darkSurface,
    onPrimary    = darkOnPrimary,
    onSecondary  = Color(0xFFAC378F),
    onTertiary   = Color(0xFFAC378F),
    onBackground = darkOnBackground,
    onSurface    = darkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary      = lightPrimary,
    secondary    = PurpleGrey40,
    tertiary     = Pink40,
    background   = lightBackground,
    surface      = lightSurface,
    onPrimary    = lightOnPrimary,
    onSecondary  = Navy,
    onTertiary   = Color(0xFFAC378F),
    onBackground = lightOnBackground,
    onSurface    = lightOnSurface
)

@Composable
fun SMSAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit) {

    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
