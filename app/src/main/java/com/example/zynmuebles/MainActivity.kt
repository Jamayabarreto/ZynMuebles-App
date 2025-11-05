package com.example.zynmuebles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zynmuebles.ui.theme.ZynMueblesTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZynMueblesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5)
                ) {
                    LoginScreen(
                        onLoginSuccess = {
                            Toast.makeText(
                                this,
                                "¡Sesión iniciada con éxito!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        },
                        onNavigateToRegister = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                        },
                        onForgotPassword = {
                            startActivity(Intent(this, ForgotPasswordActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore

    // Configurar Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher para Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            isLoading = true
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    isLoading = false
                    if (authTask.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Verificar si el usuario ya existe en Firestore
                            db.collection("usuarios").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    if (!document.exists()) {
                                        // Es un usuario nuevo, crear su documento
                                        val userData = hashMapOf(
                                            "uid" to user.uid,
                                            "nombre" to (user.displayName ?: "Usuario Google"),
                                            "email" to (user.email ?: ""),
                                            "rol" to "cliente",
                                            "fechaCreacion" to System.currentTimeMillis()
                                        )

                                        db.collection("usuarios")
                                            .document(user.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                onLoginSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Error al crear perfil: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        // Usuario existente
                                        onLoginSuccess()
                                    }
                                }
                        } else {
                            onLoginSuccess()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${authTask.exception?.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } catch (e: ApiException) {
            isLoading = false
            Log.e("GoogleSignIn", "Error: ${e.message}")
            Toast.makeText(context, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
        }
    }

    // Colores personalizados
    val primaryColor = Color(0xFFD87057)
    val cardBackground = Color(0xFFD4B5B0)
    val lightBackground = Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título superior
        Text(
            text = "BIENVENIDO A ZYNMUEBLES",
            fontSize = 16.sp,
            color = Color.Gray,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Card principal con fondo rosado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono del sillón
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(top = 16.dp, bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = primaryColor
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "🛋",
                            fontSize = 48.sp
                        )
                    }
                }

                // Texto "INGRESA A TU CUENTA"
                Text(
                    text = "INGRESA\nA TU CUENTA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Campo EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("EMAIL") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = primaryColor
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = primaryColor.copy(alpha = 0.5f),
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo CONTRASEÑA
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("CONTRASEÑA") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = primaryColor
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = primaryColor.copy(alpha = 0.5f),
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Botón INICIAR SESIÓN
                Button(
                    onClick = {
                        when {
                            email.isBlank() || password.isBlank() -> {
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
                            else -> {
                                isLoading = true
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            onLoginSuccess()
                                        } else {
                                            val errorMessage = when (task.exception) {
                                                is FirebaseAuthInvalidUserException ->
                                                    "Usuario no encontrado"
                                                is FirebaseAuthInvalidCredentialsException ->
                                                    "Contraseña incorrecta"
                                                else ->
                                                    "Error: ${task.exception?.localizedMessage}"
                                            }
                                            Toast.makeText(
                                                context,
                                                errorMessage,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    ),
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
                        Text(
                            text = "INICIAR SESIÓN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Separador "O"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                    Text(
                        text = "O",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                }

                // Botón GOOGLE SIGN-IN
                OutlinedButton(
                    onClick = {
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continuar con Google",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Link "¿olvidaste tu contraseña?"
                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "¿olvidaste tu contraseña?",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }

                // Indicador de página (punto)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(50.dp))
                        .padding(bottom = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón CREAR CUENTA (fuera del card)
        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "CREAR CUENTA",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = android.R.drawable.ic_input_add),
                contentDescription = "Crear cuenta",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}