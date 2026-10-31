package cl.duoc.alba_Lab.data

import cl.duoc.alba_Lab.model.Solicitud
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(private val dao: SolicitudDao) {
    suspend fun buscarSolicitud(id: String) = dao.buscarSolicitud(id)
    val solicitudes: Flow<List<Solicitud>> = dao.obtenerSolicitudes()//entregamos la lista actualizada cuando se cambian las sol
    suspend fun guardarSolicitud(solicitud: Solicitud){dao.guardarSolicitud(solicitud)}//crea una sol o guarda sus cambios usando mismo id
    suspend fun eliminarSolicitud(id: String){dao.eliminarSolicitud(id)}//borra sol por id
}
