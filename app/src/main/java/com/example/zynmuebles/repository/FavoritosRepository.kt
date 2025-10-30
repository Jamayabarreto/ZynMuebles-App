package com.example.zynmuebles.repository

import com.example.zynmuebles.model.Mueble
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FavoritosRepository {

    // Cambiado para usar la inicialización recomendada 'by lazy'
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // --- ¡AQUÍ ESTÁ LA MEJORA QUE PEDISTE! ---
    // Creamos una referencia base a la colección de favoritos del usuario actual.
    // Es 'nullable' porque el usuario podría no haber iniciado sesión.
    private fun getUserFavoritesRef(): CollectionReference? {
        val userId = auth.currentUser?.uid
        // Si no hay userId, devolvemos null para manejarlo de forma segura.
        return userId?.let {
            firestore.collection("usuarios").document(it).collection("favoritos")
        }
    }
    // ------------------------------------------

    /**
     * Añade o elimina un mueble de la lista de favoritos del usuario actual.
     */
    fun toggleFavorito(mueble: Mueble, onResult: (fueAgregado: Boolean) -> Unit) {
        // Obtenemos la referencia a los favoritos. Si es null, salimos.
        val userFavoritesRef = getUserFavoritesRef() ?: return

        // Ahora la referencia al documento del mueble es más corta y legible.
        val favoritoDocRef = userFavoritesRef.document(mueble.id)

        favoritoDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Ya está en favoritos → lo eliminamos.
                favoritoDocRef.delete().addOnSuccessListener {
                    onResult(false) // Indica que fue eliminado.
                }
            } else {
                // No está en favoritos → lo agregamos.
                favoritoDocRef.set(mueble).addOnSuccessListener {
                    onResult(true) // Indica que fue agregado.
                }
            }
        }
    }

    /**
     * Verifica si un mueble específico ya está en los favoritos del usuario.
     */
    fun verificarFavorito(muebleId: String, onResult: (esFavorito: Boolean) -> Unit) {
        // Obtenemos la referencia a los favoritos. Si es null, el mueble no es favorito.
        val userFavoritesRef = getUserFavoritesRef()
        if (userFavoritesRef == null) {
            onResult(false)
            return
        }

        // De nuevo, la consulta es más limpia y segura.
        userFavoritesRef.document(muebleId)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.exists()) // Devuelve true si el documento existe.
            }
            .addOnFailureListener {
                onResult(false) // Si hay un error, asumimos que no es favorito.
            }
    }
}