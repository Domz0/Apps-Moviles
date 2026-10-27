package cl.duoc.alba_Lab.model

data class PerfilUiState(
    val id: String? = null,
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val revisionFoto: Int = 0,
    val tipoCuenta: String = "",
    val empresa: String = "",
    val errorEmpresa: String? = null
)
