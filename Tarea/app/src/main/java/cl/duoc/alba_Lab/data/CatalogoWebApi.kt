package cl.duoc.alba_Lab.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
data class ProductoWeb(val id: Int, val nombre: String, val descripcion: String, val precio: Int)

data class ProgramaWeb(val id: String, val nombre: String, val descripcion: String)
data class SolicitudInvitadoWeb(val modalidad: String, val mensaje: String)
interface CatalogoWebApi {

    @GET("api/productos")
    suspend fun obtenerProductos():List<ProductoWeb>

    @GET("api/programas")
    suspend fun obtenerProgramas(): List<ProgramaWeb>

    @POST("api/solicitudes/invitado")
    suspend fun enviarSolicitudInvitado(@Body solicitud: SolicitudInvitadoWeb): Response<Unit>
}
object AlbaLabApi {
    val catalogo: CatalogoWebApi by lazy{
        Retrofit.Builder()
            .baseUrl("https://alba-catalogo-api.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogoWebApi::class.java)
    }
}