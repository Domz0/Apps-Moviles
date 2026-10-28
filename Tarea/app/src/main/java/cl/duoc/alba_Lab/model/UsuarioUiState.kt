package cl.duoc.alba_Lab.model

// Estado del formulario: guarda los datos ingresados y sus errores.
data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val tipoColaborador: String = "",
    val empresa: String = "",
    val modalidad: String = "",
    val mensaje: String = "",
    val esInvitado: Boolean = false,
    val imagenes: List<String> = emptyList(),
    val procesando: Boolean = false,
    val aviso: String? = null,

    val errores: UsuarioErrores = UsuarioErrores()
)
