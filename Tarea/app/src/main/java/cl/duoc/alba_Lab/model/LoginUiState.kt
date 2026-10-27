package cl.duoc.alba_Lab.model

// Datos y errores propios del inicio de sesión.
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val datosValidos: Boolean = false
)
