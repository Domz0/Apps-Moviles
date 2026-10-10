package cl.duoc.alba_Lab.model

// Errores por campo: null indica que no hay un mensaje de error.
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val direccion: String? = null
)
