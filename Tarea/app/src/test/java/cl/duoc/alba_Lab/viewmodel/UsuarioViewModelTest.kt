package cl.duoc.alba_Lab.viewmodel

import cl.duoc.alba_Lab.PruebaViewModel
import cl.duoc.alba_Lab.data.*
import cl.duoc.alba_Lab.model.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.*
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class UsuarioViewModelTest : PruebaViewModel() {
    private lateinit var solicitudesLocales: SolicitudRepository
    private lateinit var cuentasLocales: CuentaRepository
    private lateinit var fotos: ImagenStorage
    private lateinit var catalogoWeb: CatalogoWebRepository
    private lateinit var cuentasWeb: CuentasWebRepository
    private lateinit var viewModel: UsuarioViewModel

    private val cuenta = Cuenta("local-1", "test1", "test@duoc.cl", "Persona", direccion = "Calle 123", aceptaTerminos = true)
    private val cuentaRemota = CuentaWeb(1, "test1", "test@duoc.cl")
    private val solicitudRemota = SolicitudCuentaWeb(10, "Donación", "Quiero colaborar")

    private fun errorHttp(codigo: Int) = HttpException(Response.error<Unit>(codigo, "{}".toResponseBody()))

    private suspend fun entrar() {
        viewModel.iniciarSesion("test@duoc.cl", "secreto") shouldBe true
        completarTareas()
    }

    private fun completarRegistro() {
        viewModel.actualizarRegistro {
            it.copy(nombre = "test1", correo = "test@duoc.cl", clave = "secreto",
                direccion = "Calle 123", tipoColaborador = "Persona", aceptaTerminos = true)
        }
    }

    private fun completarInvitado() {
        viewModel.nuevaSolicitud(invitado = true)
        viewModel.onNombreChange("test1")
        viewModel.onCorreoChange("test@duoc.cl")
        viewModel.onTipoColaboradorChange("Persona")
        viewModel.onTerminosChange(true)
        viewModel.onModalidadChange("Donación")
        viewModel.onMensajeChange("Quiero colaborar")
    }

    private suspend fun cargarUnaSolicitud() {
        entrar()
        coEvery { cuentasWeb.obtenerSolicitudes() } returns listOf(solicitudRemota)
        viewModel.cargarSolicitudes()
        completarTareas()
    }

    init {
        beforeTest {
            solicitudesLocales = mockk(relaxed = true)
            cuentasLocales = mockk(relaxed = true)
            fotos = mockk(relaxed = true)
            catalogoWeb = mockk()
            cuentasWeb = mockk(relaxed = true)
            coEvery { cuentasLocales.buscarCuenta(any()) } returns cuenta
            coEvery { solicitudesLocales.buscarSolicitud(any()) } returns null
            coEvery { cuentasWeb.iniciarSesion(any(), any()) } returns cuentaRemota
            coEvery { cuentasWeb.registrarCuenta(any(), any(), any()) } returns cuentaRemota
            coEvery { cuentasWeb.obtenerSolicitudes() } returns emptyList()
            viewModel = registrar(UsuarioViewModel(solicitudesLocales, cuentasLocales, fotos, catalogoWeb, cuentasWeb))
        }

        test("el registro vacio indica los campos obligatorios y no llama al servidor") {
            viewModel.registrarCuenta() shouldBe false
            val errores = viewModel.registro.value.errores
            errores.nombre.shouldNotBeNull()
            errores.correo.shouldNotBeNull()
            errores.clave.shouldNotBeNull()
            errores.direccion.shouldNotBeNull()
            errores.tipoColaborador.shouldNotBeNull()
            errores.aceptaTerminos.shouldNotBeNull()
            coVerify(exactly = 0) { cuentasWeb.registrarCuenta(any(), any(), any()) }
        }

        test("un registro valido abre sesion y conserva la cuenta local") {
            completarRegistro()
            viewModel.registrarCuenta() shouldBe true
            completarTareas()
            viewModel.sesion.value shouldBe cuenta
            viewModel.registro.value shouldBe UsuarioUiState()
            coVerify { cuentasWeb.registrarCuenta("test1", "test@duoc.cl", "secreto") }
            coVerify { cuentasLocales.actualizarCuenta(cuenta) }
        }

        test("credenciales rechazadas no abren sesión") {
            coEvery { cuentasWeb.iniciarSesion(any(), any()) } throws errorHttp(401)
            viewModel.iniciarSesion("test@duoc.cl", "incorrecta") shouldBe false
            viewModel.sesion.value shouldBe null
            verify { cuentasWeb.cerrarSesion() }
        }

        test("cerrar sesión limpia perfil formulario y solicitudes") {
            cargarUnaSolicitud()
            viewModel.cerrarSesion()
            completarTareas()
            viewModel.sesion.value shouldBe null
            viewModel.solicitudes.value shouldBe emptyList()
            viewModel.perfil.value shouldBe PerfilUiState()
            viewModel.estado.value shouldBe UsuarioUiState()
        }

        test("guardar el perfil actualiza servidor y copia local") {
            entrar()
            coEvery { cuentasWeb.actualizarPerfil("test2", "test@duoc.cl", "secreto") } returns cuentaRemota.copy(nombre = "test2")
            viewModel.onPerfilNombreChange("test2")
            viewModel.guardarPerfil()
            completarTareas()
            viewModel.sesion.value?.nombre shouldBe "test2"
            viewModel.sesion.value?.id shouldBe cuenta.id
            viewModel.perfil.value.guardando shouldBe false
            coVerify { cuentasLocales.actualizarCuenta(match { it.nombre == "test2" && it.id == cuenta.id }) }
        }

        test("un mensaje mayor a mil caracteres no se envia") {
            completarInvitado()
            viewModel.onMensajeChange("a".repeat(1001))
            viewModel.guardarSolicitud() shouldBe false
            coVerify(exactly = 0) { catalogoWeb.enviarSolicitudInvitado(any(), any()) }
        }

        test("permite exactamente mil caracteres") {
            completarInvitado()
            val mensaje = "a".repeat(1000)
            viewModel.onMensajeChange(mensaje)
            coEvery { catalogoWeb.enviarSolicitudInvitado("Donación", mensaje) } returns true
            viewModel.guardarSolicitud() shouldBe true
            viewModel.estado.value.procesando shouldBe false
        }

        test("crear una solicitud usa el id remoto y guarda una copia local") {
            entrar()
            viewModel.nuevaSolicitud()
            viewModel.onModalidadChange("Donación")
            viewModel.onMensajeChange("  Quiero colaborar  ")
            coEvery { cuentasWeb.guardarSolicitud(null, "Donación", "Quiero colaborar") } returns solicitudRemota
            viewModel.guardarSolicitud() shouldBe true
            viewModel.solicitudes.value.single().id shouldBe "api_10"
            coVerify { solicitudesLocales.guardarSolicitud(match { it.id == "api_10" && it.cuentaId == cuenta.id }) }
        }

        test("editar una solicitud conserva su id remoto") {
            cargarUnaSolicitud()
            viewModel.editarSolicitud("api_10") shouldBe true
            viewModel.onMensajeChange("Nuevo mensaje")
            coEvery { cuentasWeb.guardarSolicitud(10, "Donación", "Nuevo mensaje") } returns solicitudRemota.copy(mensaje = "Nuevo mensaje")
            viewModel.guardarSolicitud() shouldBe true
            viewModel.solicitudes.value.single().mensaje shouldBe "Nuevo mensaje"
            coVerify { cuentasWeb.guardarSolicitud(10, "Donación", "Nuevo mensaje") }
        }

        test("eliminar una solicitud confirmada limpia la lista y la copia local") {
            cargarUnaSolicitud()
            viewModel.eliminarSolicitud("api_10")
            completarTareas()
            viewModel.solicitudes.value shouldBe emptyList()
            coVerify { cuentasWeb.eliminarSolicitud(10) }
            coVerify { solicitudesLocales.eliminarSolicitud("api_10") }
        }
    }
}

