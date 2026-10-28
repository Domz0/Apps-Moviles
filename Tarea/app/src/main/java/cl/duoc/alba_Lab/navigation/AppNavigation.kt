package cl.duoc.alba_Lab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import cl.duoc.alba_Lab.ui.screens.AlbaScaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.alba_Lab.ui.screens.HomeScreen
import cl.duoc.alba_Lab.ui.screens.LoginScreen
import cl.duoc.alba_Lab.ui.screens.ProfileScreen
import cl.duoc.alba_Lab.ui.screens.EditarPerfilScreen
import cl.duoc.alba_Lab.ui.screens.RegistroScreen
import cl.duoc.alba_Lab.ui.screens.CrearSolicitudScreen
import cl.duoc.alba_Lab.data.FotoPerfilStorage
import cl.duoc.alba_Lab.ui.screens.ResumenScreen
import cl.duoc.alba_Lab.ui.screens.SettingsScreen
import cl.duoc.alba_Lab.ui.screens.CatalogoScreen
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import cl.duoc.alba_Lab.viewmodel.TemaViewModel
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import cl.duoc.alba_Lab.ui.screens.SolicitudesScreen
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.alba_Lab.data.AlbaDataBase
import cl.duoc.alba_Lab.data.SolicitudRepository
import cl.duoc.alba_Lab.viewmodel.CarritoViewModel
import cl.duoc.alba_Lab.ui.screens.QueHacemosScreen
import cl.duoc.alba_Lab.ui.screens.ContactoScreen

@Composable
private fun obtenerUsuarioViewModel(): UsuarioViewModel {
    val context = LocalContext.current.applicationContext
    return viewModel(factory= viewModelFactory {
        initializer {
            val database = AlbaDataBase.obtenerDatabase(context)
            val repository = SolicitudRepository(database.solicitudDao())
            UsuarioViewModel(repository, FotoPerfilStorage(context, "solicitud_"))}
    })
}

@Composable
fun AppNavigation(temaViewModel: TemaViewModel, usuarioViewModel: UsuarioViewModel = obtenerUsuarioViewModel()) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()
    val sesion by usuarioViewModel.sesion.collectAsState()
    val formulario by usuarioViewModel.estado.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let { screen ->
                            popUpTo(screen.route) { inclusive = event.inclusive }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                NavigationEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    // faltan agregar un par de ventanas
    AlbaScaffold(mainViewModel, usuarioViewModel) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(mainViewModel) }
            composable(Screen.Login.route) {
                if (sesion == null) LoginScreen(mainViewModel, viewModel(), usuarioViewModel)
                else LaunchedEffect(Unit) { navController.navigate(Screen.Profile.route) { popUpTo(Screen.Home.route); launchSingleTop = true } }
            }
            composable(Screen.Profile.route) {
                if (sesion != null) ProfileScreen(mainViewModel, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.popBackStack(Screen.Home.route, false) }
            }
            composable(Screen.EditarPerfil.route) {
                if (sesion != null) EditarPerfilScreen(mainViewModel, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.popBackStack(Screen.Home.route, false) }
            }
            composable(Screen.Settings.route) { SettingsScreen(mainViewModel, temaViewModel) }
            composable(Screen.Registro.route) {
                if (sesion == null) RegistroScreen(navController, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.navigate(Screen.Profile.route) { popUpTo(Screen.Home.route); launchSingleTop = true } }
            }
            composable(Screen.CrearSolicitud.route) {
                if (sesion != null && !formulario.esInvitado) CrearSolicitudScreen(navController, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.popBackStack(Screen.Home.route, false) }
            }
            composable(Screen.Invitado.route) {
                if (sesion == null && formulario.esInvitado) CrearSolicitudScreen(navController, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.popBackStack(Screen.Home.route, false) }
            }
            composable(Screen.Resumen.route) {
                if (formulario.esInvitado || sesion != null) ResumenScreen(navController, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.popBackStack(Screen.Home.route, false) }
            }
            composable(Screen.Catalogo.route) { CatalogoScreen(mainViewModel, carritoViewModel) }
            composable(Screen.QueHacemos.route) { QueHacemosScreen(mainViewModel) }
            composable(Screen.Contacto.route) { ContactoScreen(mainViewModel) }
            composable(Screen.Solicitudes.route) {
                if (sesion != null) SolicitudesScreen(navController, usuarioViewModel)
                else LaunchedEffect(Unit) { navController.popBackStack(Screen.Home.route, false) }
            }
            composable(Screen.HistorialDebug.route) { SolicitudesScreen(navController, usuarioViewModel, debug = true) }
        }
    }
}
