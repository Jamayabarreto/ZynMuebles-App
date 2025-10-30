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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZynMueblesTheme {
                EditProfileScreen(onNavigateBack = { finish() })
            }
        }
    }
}

@Composable
fun EditProfileScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val storage = Firebase.storage
    val currentUser = auth.currentUser
    val scrollState = rememberScrollState()
    val primaryColor = Color(0xFFD87057)

    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoUrl by remember { mutableStateOf(currentUser?.photoUrl?.toString() ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { photoUri = it } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
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
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.Black)
                }
                Text(
                    "Editar Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(140.dp)) {
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    if (photoUri != null || photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = photoUri ?: photoUrl,
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
                                Icons.Default.Person,
                                "Sin foto",
                                modifier = Modifier.size(70.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.BottomEnd)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    shape = CircleShape,
                    color = primaryColor,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "Cambiar foto",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Cambiar foto de perfil", color = primaryColor, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Nombre",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                placeholder = { Text("Ingresa tu nombre") },
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

            Text(
                "Correo electrónico",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = currentUser?.email ?: "",
                onValueChange = { },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.LightGray,
                    disabledTextColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    if (displayName.isBlank()) {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    if (photoUri != null && currentUser != null) {
                        val storageRef = storage.reference.child("profile_images/${currentUser.uid}.jpg")
                        storageRef.putFile(photoUri!!).addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { uri ->
                                updateProfile(displayName, uri.toString(), onNavigateBack, context) {
                                    isLoading = false
                                }
                            }
                        }.addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        updateProfile(displayName, photoUrl, onNavigateBack, context) {
                            isLoading = false
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
                    Text("GUARDAR CAMBIOS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun updateProfile(
    name: String,
    photo: String,
    onSuccess: () -> Unit,
    context: android.content.Context,
    onComplete: () -> Unit
) {
    val user = Firebase.auth.currentUser
    val updates = UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .apply { if (photo.isNotEmpty()) setPhotoUri(Uri.parse(photo)) }
        .build()

    user?.updateProfile(updates)?.addOnCompleteListener {
        onComplete()
        if (it.isSuccessful) {
            user.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            }
        } else {
            Toast.makeText(context, "Error: ${it.exception?.message}", Toast.LENGTH_LONG).show()
        }
    }
}