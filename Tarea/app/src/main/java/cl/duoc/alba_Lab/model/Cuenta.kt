package cl.duoc.alba_Lab.model

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cuentas", indices = [Index(value = ["correo"], unique = true)])
data class Cuenta(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String,
    val tipo: String,
    val empresa: String = "",
    @ColumnInfo(defaultValue = "''") val direccion: String = "",
    @ColumnInfo(defaultValue = "0") val aceptaTerminos: Boolean = false
)
