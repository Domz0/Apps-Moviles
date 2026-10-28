package cl.duoc.alba_Lab.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cuentas", indices = [Index(value = ["correo"], unique = true)])
data class Cuenta(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String,
    val tipo: String,
    val empresa: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false
)
