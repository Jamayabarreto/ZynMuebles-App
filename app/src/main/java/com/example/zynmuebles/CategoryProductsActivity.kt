package com.example.zynmuebles

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zynmuebles.model.Mueble
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
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val products = remember { mutableStateListOf<Mueble>() }
    var isAdmin by remember { mutableStateOf(false) }

    // Verificar si es admin
    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    isAdmin = document.getString("rol") == "admin"
                }
        }
    }

    LaunchedEffect(categoryId) {
        if (categoryId.isNotEmpty()) {
            db.collection("muebles")
                .whereEqualTo("categoria", categoryId)
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
                            text = "${products.size} productos disponibles",
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
                            text = "🛋",
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
                    items(products) { mueble ->
                        CategoryProductCard(mueble = mueble, isAdmin = isAdmin)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryProductCard(mueble: Mueble, isAdmin: Boolean) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val primaryColor = Color(0xFFD87057)
    val context = LocalContext.current

    var isFavorite by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId, mueble.nombre) {
        if (userId != null) {
            db.collection("favoritos").document(userId).collection("muebles")
                .whereEqualTo("nombre", mueble.nombre)
                .addSnapshotListener { snapshot, _ ->
                    isFavorite = snapshot != null && !snapshot.isEmpty
                }
        }
    }

    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        .format(mueble.precio)
        .replace("COP", "$")
        .replace(",00", "")

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar mueble") },
            text = { Text("¿Estás seguro de que deseas eliminar '${mueble.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        db.collection("muebles")
                            .whereEqualTo("nombre", mueble.nombre)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (doc in documents) {
                                    db.collection("muebles").document(doc.id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Mueble eliminado", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isAdmin) 240.dp else 220.dp)
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
                            model = mueble.imageUrl,
                            contentDescription = mueble.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (mueble.categoria) {
                                    "sofas" -> "🛋"
                                    "sillas" -> "🪑"
                                    "mesas" -> "🪵"
                                    "camas" -> "🛏"
                                    "decoracion" -> "🖼"
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
                        text = mueble.nombre,
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

                    // Botón de eliminar (solo para admins)
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.1f)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Eliminar",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Botón de favoritos
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
                        if (userId != null) {
                            val favRef = db.collection("favoritos")
                                .document(userId)
                                .collection("muebles")

                            if (isFavorite) {
                                favRef.whereEqualTo("nombre", mueble.nombre)
                                    .get()
                                    .addOnSuccessListener { docs ->
                                        for (doc in docs) {
                                            favRef.document(doc.id).delete()
                                        }
                                    }
                            } else {
                                favRef.add(mueble)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}