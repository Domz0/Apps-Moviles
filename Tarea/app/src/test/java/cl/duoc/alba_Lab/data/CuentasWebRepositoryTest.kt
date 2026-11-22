package cl.duoc.alba_Lab.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.*
import okhttp3.Credentials
import java.io.IOException

class CuentasWebRepositoryTest : FunSpec({
    lateinit var api: CuentasWebApi
    lateinit var cliente: ClienteCuentasWeb
    lateinit var repository: CuentasWebRepository
    val cuenta = CuentaWeb(1, "test1", "test@duoc.cl")
    val autorizacion = Credentials.basic("test@duoc.cl", "secreto", Charsets.UTF_8)
    val csrf = mapOf("X-CSRF-TOKEN" to "token-prueba")

    beforeTest {
        api = mockk(relaxed = true)
        cliente = mockk(relaxed = true)
        every { cliente.api } returns api
        coEvery { api.obtenerCuenta(any()) } returns cuenta
        coEvery { api.obtenerCsrf(any()) } returns TokenCsrfWeb("X-CSRF-TOKEN", "token-prueba")
        repository = CuentasWebRepository { cliente }
    }

    test("crear una solicitud envía el token csrf y recorta espacios") {
        repository.iniciarSesion("test@duoc.cl", "secreto")
        repository.guardarSolicitud(null, " Donación ", " Mensaje ")
        coVerify { api.crearSolicitud(autorizacion, csrf, DatosSolicitudWeb("Donación", "Mensaje")) }
    }

    test("un login fallido no conserva una sesion anterior") {
        repository.iniciarSesion("test@duoc.cl", "secreto")
        coEvery { api.obtenerCuenta(any()) } throws IOException()
        shouldThrow<IOException> { repository.iniciarSesion("test2@duoc.cl", "incorrecta") }
        shouldThrow<IllegalStateException> { repository.obtenerSolicitudes() }
    }
})

