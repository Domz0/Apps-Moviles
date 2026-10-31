package cl.duoc.alba_Lab.model

import androidx.room.Entity

@Entity(tableName = "carrito", primaryKeys = ["cuentaId", "productoId"])
data class CarritoGuardado(
    val cuentaId: String,
    val productoId: String,
    val cantidad: Int
)
