package com.example.zynmuebles

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zynmuebles.model.User
import com.example.zynmuebles.ui.theme.ZynMueblesTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZynMueblesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    RegisterScreen(
                        onRegisterSuccess = {
                            Toast.makeText(
                                this,
                                "¡Registro exitoso! Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        },
                        onNavigateBack = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore
    val scrollState = rememberScrollState()

    val primaryColor = Color(0xFFD87057)
    val darkGray = Color(0xFF666666)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.Black,
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(16.dp),
            color = primaryColor
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "🛋", fontSize = 40.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CREA TU CUENTA",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = darkGray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo Nombre
        Text(
            text = "Nombre",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Ingrese su nombre", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Nombre",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Apellido
        Text(
            text = "Apellido",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            placeholder = { Text("Ingrese su primer apellido", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Apellido",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Email
        Text(
            text = "Email",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Ingrese su correo electrónico", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Contraseña
        Text(
            text = "Crea tu contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    "Tu contraseña debe tener mínimo 10 caracteres",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Contraseña",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "👁" else "👁‍🗨",
                        fontSize = 20.sp
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Confirmar Contraseña
        Text(
            text = "Confirma tu contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("Confirma tu nueva contraseña", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Confirmar Contraseña",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Text(
                        text = if (confirmPasswordVisible) "👁" else "👁‍🗨",
                        fontSize = 20.sp
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Botón de registrarse
        FloatingActionButton(
            onClick = {
                when {
                    nombre.isBlank() || apellido.isBlank() ||
                            email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                        Toast.makeText(
                            context,
                            "Por favor completa todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        Toast.makeText(
                            context,
                            "Email inválido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    password.length < 10 -> {
                        Toast.makeText(
                            context,
                            "La contraseña debe tener al menos 10 caracteres",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    password != confirmPassword -> {
                        Toast.makeText(
                            context,
                            "Las contraseñas no coinciden",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        isLoading = true

                        // Registrar en Firebase Auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid

                                    if (userId != null) {
                                        // Verificar si es el primer usuario
                                        db.collection("usuarios")
                                            .get()
                                            .addOnSuccessListener { snapshot ->
                                                val rol = if (snapshot.isEmpty) "admin" else "cliente"

                                                // Crear objeto usuario
                                                val user = hashMapOf(
                                                    "uid" to userId,
                                                    "nombre" to "$nombre $apellido",
                                                    "email" to email,
                                                    "rol" to rol,
                                                    "fechaCreacion" to System.currentTimeMillis()
                                                )

                                                // Guardar en Firestore
                                                db.collection("usuarios")
                                                    .document(userId)
                                                    .set(user)
                                                    .addOnSuccessListener {
                                                        isLoading = false
                                                        val mensaje = if (rol == "admin") {
                                                            "¡Cuenta de administrador creada!"
                                                        } else {
                                                            "¡Cuenta creada exitosamente!"
                                                        }
                                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                                        onRegisterSuccess()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        isLoading = false
                                                        Toast.makeText(
                                                            context,
                                                            "Error al guardar usuario: ${e.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                Toast.makeText(
                                                    context,
                                                    "Error al verificar usuarios: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Error: No se pudo obtener el ID del usuario",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Error al registrar: ${task.exception?.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
            },
            containerColor = Color.Black,
            modifier = Modifier.size(64.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Registrarse",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}