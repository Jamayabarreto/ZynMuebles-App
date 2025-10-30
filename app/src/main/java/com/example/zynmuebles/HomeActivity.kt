package com.example.zynmuebles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zynmuebles.model.Mueble
import com.example.zynmuebles.ui.theme.ZynMueblesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZynMueblesTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFD87057)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> MueblesScreen()
                1 -> FavoritosScreen()
                2 -> ProfileScreen(onNavigateToLogin = {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun MueblesScreen() {
    val db = FirebaseFirestore.getInstance()
    val muebles = remember { mutableStateListOf<Mueble>() }

    // 🔥 Cargar los muebles en tiempo real
    LaunchedEffect(Unit) {
        db.collection("muebles").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                muebles.clear()
                muebles.addAll(snapshot.toObjects(Mueble::class.java))
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(muebles) { mueble ->
            MuebleCard(mueble)
        }
    }
}

@Composable
fun FavoritosScreen() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val favoritos = remember { mutableStateListOf<Mueble>() }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("favoritos").document(userId).collection("muebles")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        favoritos.clear()
                        favoritos.addAll(snapshot.toObjects(Mueble::class.java))
                    }
                }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favoritos) { mueble ->
            MuebleCard(mueble, esFavorito = true)
        }
    }
}

@Composable
fun MuebleCard(mueble: Mueble, esFavorito: Boolean = false) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = mueble.imageUrl,
                contentDescription = mueble.nombre,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = mueble.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = mueble.descripcion, fontSize = 13.sp, color = Color.Gray)
                Text(
                    text = "$${mueble.precio}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD87057)
                )
            }

            // 💖 Botón de favorito
            Icon(
                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorito",
                tint = Color(0xFFD87057),
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        if (userId != null) {
                            val favRef = db.collection("favoritos")
                                .document(userId)
                                .collection("muebles")

                            if (esFavorito) {
                                // Si ya está en favoritos → eliminar
                                favRef.whereEqualTo("nombre", mueble.nombre)
                                    .get()
                                    .addOnSuccessListener { docs ->
                                        for (doc in docs) {
                                            favRef.document(doc.id).delete()
                                        }
                                    }
                            } else {
                                // Si no está → agregar
                                favRef.add(mueble)
                            }
                        }
                    }
            )
        }
    }
}

// ==================== PANTALLA DE PERFIL ====================

@Composable
fun ProfileScreen(onNavigateToLogin: () -> Unit) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val auth = Firebase.auth
    val primaryColor = Color(0xFFD87057)
    val backgroundColor = Color(0xFFF5F5F5)
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var profilePhotoUrl by remember { mutableStateOf(auth.currentUser?.photoUrl?.toString() ?: "") }
    var displayName by remember { mutableStateOf(auth.currentUser?.displayName ?: "Usuario") }

    DisposableEffect(Unit) {
        val listener = {
            currentUser = auth.currentUser
            profilePhotoUrl = auth.currentUser?.photoUrl?.toString() ?: ""
            displayName = auth.currentUser?.displayName ?: "Usuario"
        }
        onDispose { }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = primaryColor
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = "🛋", fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Mi Perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val intent = Intent(context, EditProfileActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                if (profilePhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profilePhotoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = displayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentUser?.email ?: "correo@ejemplo.com",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfileMenuSection(
            title = "INFORMACIÓN PERSONAL",
            subtitle = "Información de los documentos de identidad y propietario",
            icon = Icons.Default.Person,
            onClick = {
                Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ProfileMenuSection(
            title = "DATOS DE PAGO Y HERRAMIENTA",
            subtitle = "Agrega un método seguro de tus pagos",
            icon = Icons.Default.AccountCircle,
            onClick = {
                Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ProfileMenuSection(
            title = "SEGURIDAD",
            subtitle = "Cambia periódicamente tu contraseña",
            icon = Icons.Default.Lock,
            onClick = {
                val intent = Intent(context, SecurityActivity::class.java)
                context.startActivity(intent)
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ProfileMenuSection(
            title = "TARJETAS",
            subtitle = "Tarjeta guardadas en la cuenta",
            icon = Icons.Default.Star,
            onClick = {
                Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ProfileMenuSection(
            title = "DIRECCIONES",
            subtitle = "Direcciones guardadas en tu cuenta",
            icon = Icons.Default.LocationOn,
            onClick = {
                Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ProfileMenuSection(
            title = "PRIVACIDAD",
            subtitle = "Configurar el control sobre el uso de tus datos",
            icon = Icons.Default.Settings,
            onClick = {
                Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ProfileMenuSection(
            title = "COMUNICACIONES",
            subtitle = "Elige que tipo de noticias quieres recibir",
            icon = Icons.Default.Notifications,
            onClick = {
                Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                auth.signOut()
                onNavigateToLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CERRAR SESIÓN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileMenuSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFD87057),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ir",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}