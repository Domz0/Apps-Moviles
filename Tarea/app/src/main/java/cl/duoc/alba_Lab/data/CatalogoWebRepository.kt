package cl.duoc.alba_Lab.data

class CatalogoWebRepository(private val api: CatalogoWebApi) {
    suspend fun obtenerProductos(): List<ProductoWeb> =
        api.obtenerProductos()}
