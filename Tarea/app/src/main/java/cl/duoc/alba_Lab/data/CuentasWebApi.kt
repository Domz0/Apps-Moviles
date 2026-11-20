package cl.duoc.alba_Lab.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//esto conecta las cuentas y sus solicitudes con la apipi
data class RegistroCuentaWeb(val nombre: String, val correo: String, val clave: String)
data class CuentaWeb(val id: Long, val nombre: String, val correo: String)
data class TokenCsrfWeb(val headerName: String, val token: String)
data class DatosSolicitudWeb(val modalidad: String, val mensaje: String)
data class SolicitudCuentaWeb(val id: Long, val modalidad: String, val mensaje: String)

interface CuentasWebApi {
    @POST("api/cuentas/registro")
    suspend fun registrar(@Body datos: RegistroCuentaWeb): CuentaWeb

    @GET("api/cuentas/me")
    suspend fun obtenerCuenta(@Header("Authorization") autorizacion: String): CuentaWeb

    @GET("api/cuentas/csrf")
    suspend fun obtenerCsrf(@Header("Authorization") autorizacion: String): TokenCsrfWeb

    @GET("api/solicitudes")
    suspend fun obtenerSolicitudes(@Header("Authorization") autorizacion: String): List<SolicitudCuentaWeb>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Header("Authorization") autorizacion: String, @HeaderMap proteccionCsrf: Map<String, String>, @Body datos: DatosSolicitudWeb
    ): SolicitudCuentaWeb

    @PUT("api/solicitudes/{id}")
    suspend fun editarSolicitud(@Path("id") id: Long, @Header("Authorization") autorizacion: String, @HeaderMap proteccionCsrf: Map<String, String>, @Body datos: DatosSolicitudWeb
    ): SolicitudCuentaWeb

    @PUT("api/cuentas/me")
    suspend fun actualizarPerfil(@Header("Authorization") autorizacion: String,@HeaderMap proteccionCsrf: Map<String, String>, @Body datos: RegistroCuentaWeb
    ): CuentaWeb

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminarSolicitud( @Path("id") id: Long, @Header("Authorization") autorizacion: String, @HeaderMap proteccionCsrf: Map<String, String>)
}