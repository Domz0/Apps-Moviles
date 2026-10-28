package cl.duoc.alba_Lab.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.alba_Lab.model.CarritoGuardado
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Query("SELECT * FROM carrito WHERE cuentaId = :cuentaId ORDER BY productoId")
    fun obtenerCarrito(cuentaId: String): Flow<List<CarritoGuardado>>

    @Query("SELECT * FROM carrito WHERE cuentaId = :cuentaId AND productoId = :productoId")
    suspend fun buscarProducto(cuentaId: String, productoId: String): CarritoGuardado?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: CarritoGuardado)

    @Query("DELETE FROM carrito WHERE cuentaId = :cuentaId AND productoId = :productoId")
    suspend fun eliminarProducto(cuentaId: String, productoId: String)

    @Query("DELETE FROM carrito WHERE cuentaId = :cuentaId")
    suspend fun vaciarCarrito(cuentaId: String)
}
