package com.example.zynmuebles.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "cliente",
    val fechaCreacion: Long = System.currentTimeMillis()
)