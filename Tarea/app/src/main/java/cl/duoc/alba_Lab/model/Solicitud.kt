package cl.duoc.alba_Lab.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes") //cada solicitud sera una fila de la tabla
data class Solicitud (
    @PrimaryKey //se identifica cada solicitud por su id y se conserva identificador generado con UUID
    val id: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val tipoColaborador: String,
    val empresa: String,
    val modalidad: String,
    val mensaje: String,
    val aceptaTerminos: Boolean,
    val fechaCreacion: Long? = null,
    val cuentaId: String? = null,
    val imagenes: String = "[]"

)
