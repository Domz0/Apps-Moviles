package cl.duoc.alba_Lab.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.alba_Lab.model.Cuenta

@Dao
interface CuentaDao {
    @Query("SELECT * FROM cuentas WHERE correo = :correo LIMIT 1")
    suspend fun buscarCuenta(correo: String): Cuenta?
    @Insert
    suspend fun crearCuenta(cuenta: Cuenta)
    @Update
    suspend fun actualizarCuenta(cuenta: Cuenta)
}
