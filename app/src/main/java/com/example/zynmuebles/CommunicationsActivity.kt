package com.example.zynmuebles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zynmuebles.ui.theme.ZynMueblesTheme

class CommunicationsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZynMueblesTheme {
                CommunicationsScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunicationsScreen(onBackPressed: () -> Unit) {
    val primaryColor = Color(0xFFD87057)
    val backgroundColor = Color(0xFFF5F5F5)
    val scrollState = rememberScrollState()

    var promotions by remember { mutableStateOf(true) }
    var newProducts by remember { mutableStateOf(true) }
    var orderUpdates by remember { mutableStateOf(true) }
    var newsletter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comunicaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Preferencias de Notificaciones",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Elige qué tipo de noticias quieres recibir",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            CommunicationSettingItem(
                title = "Promociones y ofertas",
                description = "Recibe notificaciones sobre descuentos especiales",
                checked = promotions,
                onCheckedChange = { promotions = it },
                primaryColor = primaryColor
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            CommunicationSettingItem(
                title = "Nuevos productos",
                description = "Entérate de los últimos productos disponibles",
                checked = newProducts,
                onCheckedChange = { newProducts = it },
                primaryColor = primaryColor
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            CommunicationSettingItem(
                title = "Actualizaciones de pedidos",
                description = "Recibe información sobre el estado de tus pedidos",
                checked = orderUpdates,
                onCheckedChange = { orderUpdates = it },
                primaryColor = primaryColor
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            CommunicationSettingItem(
                title = "Newsletter",
                description = "Boletín mensual con tendencias y consejos",
                checked = newsletter,
                onCheckedChange = { newsletter = it },
                primaryColor = primaryColor
            )
        }
    }
}

@Composable
fun CommunicationSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}