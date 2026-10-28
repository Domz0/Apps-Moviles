package cl.duoc.alba_Lab.model

import androidx.room.Entity

// Un producto puede estar una vez en el carrito de cada cuenta, con su cantidad.
@Entity(tableName = "carrito", primaryKeys = ["cuentaId", "productoId"])
data class CarritoGuardado(
    val cuentaId: String,
    val productoId: String,
    val cantidad: Int
)
