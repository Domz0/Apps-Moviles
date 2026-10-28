package cl.duoc.alba_Lab.data

import cl.duoc.alba_Lab.model.CarritoGuardado

class CarritoRepository(private val dao: CarritoDao) {
    fun obtenerCarrito(cuentaId: String) = dao.obtenerCarrito(cuentaId)
    suspend fun buscarProducto(cuentaId: String, productoId: String) = dao.buscarProducto(cuentaId, productoId)
    suspend fun guardar(item: CarritoGuardado) = dao.guardar(item)
    suspend fun eliminarProducto(cuentaId: String, productoId: String) = dao.eliminarProducto(cuentaId, productoId)
    suspend fun vaciarCarrito(cuentaId: String) = dao.vaciarCarrito(cuentaId)
}
