package cl.duoc.alba_Lab.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import cl.duoc.alba_Lab.data.*
import cl.duoc.alba_Lab.ui.screens.CatalogoScreen
import cl.duoc.alba_Lab.ui.screens.LoginScreen
import cl.duoc.alba_Lab.viewmodel.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class PantallasUiTest {
    @get:Rule
    val compose = createComposeRule()

    private lateinit var database: AlbaDataBase
    private val almacen = ViewModelStore()
    private val api = ApiCatalogoDePrueba()

    @Before
    fun preparar() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AlbaDataBase::class.java
        ).build()
    }

    @After
    fun limpiar() {
        compose.runOnIdle { almacen.clear() }
        database.close()
    }

    private fun <T : ViewModel> registrar(viewModel: T): T {
        almacen.put(viewModel.javaClass.name, viewModel)
        return viewModel
    }

    private fun mostrarCatalogo() {
        lateinit var catalogo: CatalogoWebViewModel
        lateinit var carrito: CarritoViewModel
        compose.runOnIdle {
            catalogo = registrar(CatalogoWebViewModel(CatalogoWebRepository(api)))
            carrito = registrar(CarritoViewModel(CarritoRepository(database.carritoDao()), null))
        }
        compose.setContent {
            MaterialTheme { CatalogoScreen(rememberNavController(), carrito, catalogo) }
        }
    }

    @Test
    fun unErrorDeCatalogoPermiteReintentar() {
        api.fallar = true
        mostrarCatalogo()
        compose.onNodeWithText("No se pudo cargar el catálogo.").assertIsDisplayed()
        compose.runOnIdle { api.fallar = false }
        compose.onNodeWithText("Reintentar").performClick()
        compose.onNodeWithText("No hay productos disponibles.").assertIsDisplayed()
    }

    @Test
    fun elLoginVacioMuestraLosErroresDeLosCampos() {
        lateinit var login: LoginViewModel
        lateinit var usuario: UsuarioViewModel
        compose.runOnIdle {
            val contexto = ApplicationProvider.getApplicationContext<Context>()
            login = registrar(LoginViewModel())
            usuario = registrar(UsuarioViewModel(
                SolicitudRepository(database.solicitudDao()), CuentaRepository(database.cuentaDao()),
                ImagenStorage(contexto), CatalogoWebRepository(api)
            ))
        }
        compose.setContent {
            MaterialTheme { LoginScreen(rememberNavController(), login, usuario) }
        }
        compose.onNode(hasText("Iniciar sesión") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Ingresa tu correo electrónico").assertExists()
        compose.onNodeWithText("Ingresa tu contraseña").assertExists()
    }

    private class ApiCatalogoDePrueba : CatalogoWebApi {
        var fallar = false

        override suspend fun obtenerProductos(): List<ProductoWeb> {
            if (fallar) throw IOException("sin conexión")
            return emptyList()
        }

        override suspend fun obtenerProgramas() = emptyList<ProgramaWeb>()

        override suspend fun enviarSolicitudInvitado(solicitud: SolicitudInvitadoWeb): Response<Unit> =
            Response.success(201, Unit)
    }
}
