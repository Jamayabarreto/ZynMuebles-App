package com.example.zynmuebles.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ColorPrincipal,          // Color para botones, elementos activos
    onPrimary = White,                 // Texto sobre botones primarios
    background = White,                // Fondo general de las pantallas
    onBackground = Black,              // Texto sobre el fondo general
    surface = ColorFondoTarjeta,       // Color para tarjetas o componentes "elevados"
    onSurface = Black,                 // Texto sobre tarjetas
    secondary = ColorTextoClaro,       // Puedes usarlo para textos secundarios o bordes
    outline = ColorTextoClaro          // Color para los bordes (ej. OutlinedTextField)
)


private val DarkColorScheme = darkColorScheme(
    primary = ColorPrincipal,          // Tu color de marca puede funcionar bien en ambos temas
    onPrimary = White,
    background = Black,
    onBackground = White,
    surface = Color(0xFF1C1C1E),     // Un gris oscuro estándar para superficies
    onSurface = White,
    secondary = ColorTextoClaro,
    outline = ColorTextoClaro
)


@Composable
fun ZynMueblesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Esto viene de Type.kt, lo dejamos como está
        content = content
    )
}
