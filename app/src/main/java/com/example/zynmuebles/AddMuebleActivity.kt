package com.example.zynmuebles

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zynmuebles.ui.theme.ZynMueblesTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddMuebleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZynMueblesTheme {
                AddMuebleScreen(onNavigateBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMuebleScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val storage = Firebase.storage
    val scrollState = rememberScrollState()
    val primaryColor = Color(0xFFD87057)

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var expandedCategoria by remember { mutableStateOf(false) }

    val categorias = listOf("sofas", "mesas", "sillas", "camas", "decoracion", "otros")
    val categoriasNombres = mapOf(
        "sofas" to "Sofás",
        "mesas" to "Mesas",
        "sillas" to "Sillas",
        "camas" to "Camas",
        "decoracion" to "Decoración",
        "otros" to "Otros"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { imageUri = it } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, "Volver", tint = Color.Black)
                }
                Text(
                    "Agregar Mueble",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Selector de imagen
            Text(
                "Imagen del producto",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar imagen",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Toca para seleccionar imagen", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Nombre
            Text(
                "Nombre del mueble",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Ej: Sofá moderno") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Descripción
            Text(
                "Descripción",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text("Describe el mueble") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 16.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Precio
            Text(
                "Precio",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) precio = it },
                placeholder = { Text("Precio en pesos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Dropdown Categoría
            Text(
                "Categoría",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoriasNombres[categoriaSeleccionada] ?: "Selecciona una categoría",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 32.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoriasNombres[categoria] ?: categoria) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Botón Guardar
            Button(
                onClick = {
                    when {
                        nombre.isBlank() || descripcion.isBlank() || precio.isBlank() || categoriaSeleccionada.isBlank() -> {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                        imageUri == null -> {
                            Toast.makeText(context, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isLoading = true
                            val storageRef = storage.reference.child("muebles/${System.currentTimeMillis()}.jpg")
                            storageRef.putFile(imageUri!!).addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val mueble = hashMapOf(
                                        "nombre" to nombre,
                                        "descripcion" to descripcion,
                                        "precio" to precio.toDouble(),
                                        "categoria" to categoriaSeleccionada,
                                        "imageUrl" to uri.toString()
                                    )

                                    db.collection("muebles").add(mueble)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            Toast.makeText(context, "Mueble agregado exitosamente", Toast.LENGTH_SHORT).show()
                                            onNavigateBack()
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }.addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("GUARDAR MUEBLE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}