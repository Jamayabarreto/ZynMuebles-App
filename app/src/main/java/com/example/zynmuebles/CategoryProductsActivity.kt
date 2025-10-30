package com.example.zynmuebles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// 🔥 IMPORTACIONES AÑADIDAS
import com.example.zynmuebles.model.Mueble // Asegúrate que esta es la ruta correcta
import com.example.zynmuebles.ui.theme.ZynMueblesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class CategoryProductsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = intent.getStringExtra("CATEGORY_ID") ?: ""
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Categoría"

        setContent {
            ZynMueblesTheme {
                CategoryProductsScreen(
                    categoryName = categoryName,
                    categoryId = categoryId,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    categoryName: String,
    categoryId: String,
    onBackPressed: () -> Unit
) {
    val backgroundColor = Color(0xFFF5F5F5)

    // 🔥 OBTENER PRODUCTOS DE FIREBASE
    val db = FirebaseFirestore.getInstance()
    val products = remember { mutableStateListOf<Mueble>() } // Usamos la clase Mueble

    LaunchedEffect(categoryId) {
        if (categoryId.isNotEmpty()) {
            db.collection("muebles")
                .whereEqualTo("categoria", categoryId) // Filtramos por la categoría
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        products.clear()
                        products.addAll(snapshot.toObjects(Mueble::class.java))
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = categoryName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${products.size} productos disponibles", // Se actualiza en tiempo real
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
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
        ) {
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🛋️",
                            fontSize = 60.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay productos en esta categoría",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 🔥 Actualizado para usar Mueble
                    items(products) { mueble ->
                        CategoryProductCard(mueble = mueble)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryProductCard(mueble: Mueble) { // 🔥 Recibe un objeto Mueble
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val primaryColor = Color(0xFFD87057)

    // 🔥 Lógica para saber si es favorito (igual que en HomeActivity)
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(userId, mueble.nombre) {
        if (userId != null) {
            db.collection("favoritos").document(userId).collection("muebles")
                .whereEqualTo("nombre", mueble.nombre) // Asumimos que "nombre" es único
                .addSnapshotListener { snapshot, _ ->
                    isFavorite = snapshot != null && !snapshot.isEmpty
                }
        }
    }

    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        .format(mueble.precio) // Usamos mueble.precio
        .replace("COP", "$")
        .replace(",00", "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Color(0xFFF0F0F0),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                ) {
                    if (mueble.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = mueble.imageUrl, // Usamos mueble.imageUrl
                            contentDescription = mueble.nombre, // Usamos mueble.nombre
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (mueble.categoria) { // Usamos mueble.categoria
                                    "sofas" -> "🛋️"
                                    "sillas" -> "🪑"
                                    "mesas" -> "TABLE" // Emoji de mesa no existe
                                    "camas" -> "🛏️"
                                    "decoracion" -> "🖼️"
                                    else -> "📦"
                                },
                                fontSize = 48.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = mueble.nombre, // Usamos mueble.nombre
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = priceFormatted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                IconButton(
                    onClick = {
                        // 🔥 Lógica de favoritos conectada a Firebase
                        if (userId != null) {
                            val favRef = db.collection("favoritos")
                                .document(userId)
                                .collection("muebles")

                            if (isFavorite) {
                                // Si ya es favorito -> eliminar
                                favRef.whereEqualTo("nombre", mueble.nombre)
                                    .get()
                                    .addOnSuccessListener { docs ->
                                        for (doc in docs) {
                                            favRef.document(doc.id).delete()
                                        }
                                    }
                            } else {
                                // Si no es favorito -> agregar
                                favRef.add(mueble)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else Color.Gray, // Rojo si es favorito
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}