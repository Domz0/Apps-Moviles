package cl.duoc.alba_Lab.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ProductoWeb(val id: Int, val nombre: String, val descripcion: String, val precio: Int)

data class ProgramaWeb(val id: String, val nombre: String, val descripcion: String)
interface CatalogoWebApi {
    @GET("api/productos")
    suspend fun obtenerProductos():List<ProductoWeb>

    @GET("api/programas")
    suspend fun obtenerProgramas(): List<ProgramaWeb>
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