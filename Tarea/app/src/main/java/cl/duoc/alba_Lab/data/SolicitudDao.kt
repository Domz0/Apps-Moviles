package cl.duoc.alba_Lab.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.alba_Lab.model.Solicitud
import cl.duoc.alba_Lab.model.Cuenta
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM cuentas WHERE correo = :correo LIMIT 1")
    suspend fun buscarCuenta(correo: String): Cuenta?
    @Insert
    suspend fun crearCuenta(cuenta: Cuenta)
    @Update
    suspend fun actualizarCuenta(cuenta: Cuenta)
    @Query("SELECT * FROM solicitudes WHERE id = :id LIMIT 1")
    suspend fun buscarSolicitud(id: String): Solicitud?
    @Query("SELECT * FROM solicitudes") //se entrega la lista actualizada cada que cambian las solicitudes
    fun obtenerSolicitudes(): Flow<List<Solicitud>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) //crea la solicitud o guarda sus cambios por id
    suspend fun guardarSolicitud(solicitud: Solicitud)
    @Query("DELETE FROM solicitudes WHERE id = :id") //se elimina la solicitud pro id
    suspend fun eliminarSolicitud(id: String) //por el suspend ejecutamos operaciones desde corrutina sin bloquear interfaz
}
