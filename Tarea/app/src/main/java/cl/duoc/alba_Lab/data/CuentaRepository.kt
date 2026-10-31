package cl.duoc.alba_Lab.data

import cl.duoc.alba_Lab.model.Cuenta

class CuentaRepository(private val dao: CuentaDao) {
    suspend fun buscarCuenta(correo: String) = dao.buscarCuenta(correo)
    suspend fun crearCuenta(cuenta: Cuenta) = dao.crearCuenta(cuenta)
    suspend fun actualizarCuenta(cuenta: Cuenta) = dao.actualizarCuenta(cuenta)
}
