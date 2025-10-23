package com.example.zynmuebles.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. Paleta de colores para el tema claro, usando TUS colores de Color.kt
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

// 2. Paleta de colores para el tema oscuro (opcional pero buena práctica)
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

// 3. Tu Composable de Tema, ahora más simple
@Composable
fun ZynMueblesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Seleccionamos la paleta de colores correcta
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    // Aplicamos el tema al contenido de tu app
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Esto viene de Type.kt, lo dejamos como está
        content = content
    )
}
