package com.example.smsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary      = Color(0xFFFF0000),
    secondary    = Color(0xFFFF0000),
    tertiary     = Color(0xFFFF0000),
    background   = Color(0xFFFFFBFE),
    surface      = darkSurface,
    onPrimary    = Color.White,
    onSecondary  = Color.White,
    onTertiary   = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface    = Color(0xFFEAEAEA)
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
    primary = LightBlue,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Blue,
    onSurface = White,
    onPrimary = Navy
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
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
