package cl.duoc.alba_Lab.data

import okhttp3.Credentials
import java.util.Locale

//gestiona las cuentas y solicitudes mediante la apipi
class CuentasWebRepository(private val crearCliente: () -> ClienteCuentasWeb = { ClienteCuentasWeb() }) {
    private data class SesionRemota(val cliente: ClienteCuentasWeb, val autorizacion: String)
    private var sesion: SesionRemota? = null
    suspend fun registrarCuenta(nombre: String, correo: String, clave: String
    ): CuentaWeb {
        val cliente = crearCliente()
        return cliente.api.registrar(RegistroCuentaWeb(nombre = nombre.trim(), correo = normalizarCorreo(correo), clave = clave))
    }

    suspend fun iniciarSesion(correo: String, clave: String): CuentaWeb{cerrarSesion()
        val cliente = crearCliente()
        val autorizacion = Credentials.basic(normalizarCorreo(correo),clave, Charsets.UTF_8)

        //solo conserva el acceso si el server acepta las credenciales
        val cuenta = cliente.api.obtenerCuenta(autorizacion)
        sesion = SesionRemota(cliente, autorizacion)
        return cuenta
    }
    suspend fun obtenerSolicitudes(): List<SolicitudCuentaWeb> {
        val actual = obtenerSesion()

        return actual.cliente.api.obtenerSolicitudes(actual.autorizacion)
    }
    suspend fun guardarSolicitud(id: Long?, modalidad: String, mensaje: String
    ): SolicitudCuentaWeb {
        val actual = obtenerSesion()
        val proteccion = obtenerProteccion(actual)
        val datos = DatosSolicitudWeb(modalidad = modalidad.trim(), mensaje = mensaje.trim())

        return if (id == null) {
            actual.cliente.api.crearSolicitud(actual.autorizacion,proteccion, datos)
        } else {
            actual.cliente.api.editarSolicitud(id, actual.autorizacion,proteccion, datos)
        }
    }
    suspend fun eliminarSolicitud(id: Long) {
        val actual = obtenerSesion()
        val proteccion = obtenerProteccion(actual)

        actual.cliente.api.eliminarSolicitud(id, actual.autorizacion,proteccion)
    }
    suspend fun actualizarPerfil(nombre: String, correo: String, clave: String
    ): CuentaWeb {
        val actual = obtenerSesion()
        val proteccion = obtenerProteccion(actual)

        val cuenta = actual.cliente.api.actualizarPerfil(actual.autorizacion, proteccion,
            RegistroCuentaWeb(nombre = nombre.trim(), correo = normalizarCorreo(correo), clave = clave)
        )

        //post cambio las peticiones usan las nuevas credenciales
        if (sesion === actual) {
            sesion = SesionRemota(cliente = actual.cliente,
                autorizacion = Credentials.basic(cuenta.correo, clave, Charsets.UTF_8))
        }
        return cuenta
    }
    fun cerrarSesion() {
        val anterior = sesion
        sesion = null
        anterior?.cliente?.limpiarSesion()
    }
    private fun obtenerSesion(): SesionRemota {
        return checkNotNull(sesion) {"Debes iniciar sesión para gestionar tus solicitudes."}
    }
    private suspend fun obtenerProteccion(actual: SesionRemota
    ): Map<String, String> {val csrf = actual.cliente.api.obtenerCsrf(actual.autorizacion)
        return mapOf(csrf.headerName to csrf.token)
    }
    private fun normalizarCorreo(correo: String): String {return correo.trim().lowercase(Locale.ROOT)}
}
