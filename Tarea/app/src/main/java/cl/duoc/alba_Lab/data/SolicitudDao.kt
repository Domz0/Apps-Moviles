package cl.duoc.alba_Lab.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.alba_Lab.model.Solicitud
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes") //se entrega la lista actualizada cada que cambian las solicitudes
    fun obtenerSolicitudes(): Flow<List<Solicitud>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //crea la solicitud o guarda sus cambios por id
    suspend fun guardarSolicitud(solicitud: Solicitud)

    // El perfil modifica únicamente los datos personales del registro existente.
    @Query("UPDATE solicitudes SET nombre = :nombre, correo = :correo WHERE id = :id")
    suspend fun actualizarDatosPersonales(id: String, nombre: String, correo: String): Int

    @Query("DELETE FROM solicitudes WHERE id = :id") //se elimina la solicitud pro id
    suspend fun eliminarSolicitud(id: String) //por el suspend ejecutamos operaciones desde corrutina sin bloquear interfaz
}
