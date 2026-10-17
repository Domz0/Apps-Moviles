package cl.duoc.alba_Lab.model

data class Solicitud (
    val id: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val tipoColaborador: String,
    val empresa: String,
    val modalidad: String,
    val mensaje: String,
    val aceptaTerminos: Boolean

)