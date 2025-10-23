package com.example.zynmuebles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zynmuebles.ui.theme.ZynMueblesTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZynMueblesTheme {
                HomeScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Firebase.auth.currentUser?.reload()
    }
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val primaryColor = Color(0xFFD87057)
    val backgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .verticalScroll(scrollState)
                    ) {
                        HeaderSection()
                        Spacer(modifier = Modifier.height(16.dp))
                        MainBanner()
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle(title = "SUGERENCIAS", actionText = "Ver todo")
                        Spacer(modifier = Modifier.height(12.dp))
                        SuggestionsList()
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle(title = "FAVORITOS", actionText = "")
                        Spacer(modifier = Modifier.height(12.dp))
                        FavoritesList()
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                1 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Categorías - Próximamente")
                    }
                }
                2 -> {
                    SearchScreen()
                }
                3 -> {
                    FavoritesScreen()
                }
                4 -> {
                    ProfileScreen(
                        onNavigateToLogin = {
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    val primaryColor = Color(0xFFD87057)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(12.dp),
            color = primaryColor
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "🛋", fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bienvenido a Zinmuebles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "personaliza tus muebles a tu elección",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MainBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8D5D0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛋🪴",
                    fontSize = 60.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Personaliza tu espacio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, actionText: String) {
    val primaryColor = Color(0xFFD87057)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            letterSpacing = 1.sp
        )
        if (actionText.isNotEmpty()) {
            TextButton(onClick = { }) {
                Text(
                    text = actionText,
                    fontSize = 14.sp,
                    color = primaryColor
                )
            }
        }
    }
}

@Composable
fun SuggestionsList() {
    val suggestions = listOf(
        FurnitureItem("Mesa de Centro", "💰 $250", "⭕"),
        FurnitureItem("Mesa de Noche", "💰 $180", "🟫")
    )
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(suggestions) { item ->
            ProductCard(item = item)
        }
    }
}

@Composable
fun FavoritesList() {
    val favorites = listOf(
        FurnitureItem("Sofá Moderno", "💰 $890", "🛋"),
        FurnitureItem("Estante", "💰 $320", "📚"),
        FurnitureItem("Silla Ejecutiva", "💰 $450", "💺")
    )
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favorites) { item ->
            FavoriteCard(item = item)
        }
    }
}

@Composable
fun ProductCard(item: FurnitureItem) {
    var isFavorite by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 48.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = item.price,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FavoriteCard(item: FurnitureItem) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val primaryColor = Color(0xFFD87057)
    NavigationBar(
        containerColor = Color.White,
        contentColor = primaryColor
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                indicatorColor = primaryColor.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Menu, contentDescription = "Categorías") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                indicatorColor = primaryColor.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                indicatorColor = primaryColor.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                indicatorColor = primaryColor.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                indicatorColor = primaryColor.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray
            )
        )
    }
}

data class FurnitureItem(
    val name: String,
    val price: String,
    val emoji: String
)

// ==================== PANTALLA DE BÚSQUEDA ====================

@Composable
fun SearchScreen() {
    val primaryColor = Color(0xFFD87057)
    val backgroundColor = Color(0xFFF5F5F5)

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    val filterOptions = listOf("Precio", "Color", "Material", "Estilo")

    val suggestedProducts = remember {
        listOf(
            Product("1", "Sofá beige minimalista", 1200000.0, "", "Sofá moderno", "sofas"),
            Product("2", "Sofá Chesterfield gris oscuro", 2500000.0, "", "Sofá clásico", "sofas"),
            Product("3", "Mesa Caramelo Minimalista", 850000.0, "", "Mesa moderna", "mesas")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
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
                    text = "Búsqueda",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = {
                    Text(
                        "Búsqueda de muebles...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = primaryColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        filter = filter,
                        isSelected = selectedFilters.contains(filter),
                        onToggle = {
                            selectedFilters = if (selectedFilters.contains(filter)) {
                                selectedFilters - filter
                            } else {
                                selectedFilters + filter
                            }
                        }
                    )
                }
            }

            Text(
                text = "Sugerencias",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(suggestedProducts) { product ->
                    SearchProductCard(product = product)
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    filter: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val primaryColor = Color(0xFFD87057)

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) primaryColor.copy(alpha = 0.2f) else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filter,
                fontSize = 14.sp,
                color = if (isSelected) primaryColor else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = primaryColor
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Remover",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchProductCard(product: Product) {
    val primaryColor = Color(0xFFD87057)

    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        .format(product.price)
        .replace("COP", "")
        .trim()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(96.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F0F0)
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(product.category) {
                                "sofas" -> "🛋"
                                "sillas" -> "💺"
                                "mesas" -> "⭕"
                                else -> "🛋"
                            },
                            fontSize = 40.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${priceFormatted} COP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
    }
}

// ==================== PANTALLA DE FAVORITOS ====================

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val category: String = ""
)

@Composable
fun FavoritesScreen() {
    val context = LocalContext.current
    val primaryColor = Color(0xFFD87057)
    val backgroundColor = Color(0xFFF5F5F5)

    val favoriteProducts = remember {
        listOf(
            Product("1", "Comedor con puestos varios", 3200000.0, "", "Comedor completo", "comedores"),
            Product("2", "sofá cama (cuero)", 990999.0, "", "Sofá cama de cuero", "sofas"),
            Product("3", "silla mecedora negra", 400000.0, "", "Mecedora elegante", "sillas"),
            Product("4", "Mesa para sala", 950000.0, "", "Mesa de centro moderna", "mesas"),
            Product("5", "mesa de noche", 120000.0, "", "Mesa auxiliar", "mesas"),
            Product("6", "CHAISE LONGUE", 2200000.0, "", "Sofá modular", "sofas")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
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
                Column {
                    Text(
                        text = "Favoritos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "diseños que te encantaron",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(favoriteProducts) { product ->
                FavoriteProductCard(product = product)
            }
        }
    }
}

@Composable
fun FavoriteProductCard(product: Product) {
    var isFavorite by remember { mutableStateOf(true) }
    val primaryColor = Color(0xFFD87057)

    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        .format(product.price)
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
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    if (product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when(product.category) {
                                    "sofas" -> "🛋"
                                    "sillas" -> "💺"
                                    "mesas" -> "⭕"
                                    "comedores" -> "🍽"
                                    else -> "🛋"
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
                        text = product.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,maxLines = 1,
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
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorito",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
// ==================== PANTALLA DE PERFIL ====================
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit
) {
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