package cl.duoc.alba_Lab.data

class CatalogoWebRepository(private val api: CatalogoWebApi) {
    suspend fun obtenerProductos(): List<ProductoWeb> =
        api.obtenerProductos()

    suspend fun obtenerProgramas(): List<ProgramaWeb> =
        api.obtenerProgramas()

    suspend fun enviarSolicitudInvitado(modalidad: String, mensaje: String): Boolean =
        api.enviarSolicitudInvitado(SolicitudInvitadoWeb(modalidad, mensaje)).code() == 201
}
