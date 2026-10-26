package cl.duoc.alba_Lab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import cl.duoc.alba_Lab.ui.screens.AlbaScaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.alba_Lab.ui.screens.HomeScreen
import cl.duoc.alba_Lab.ui.screens.ProfileScreen
import cl.duoc.alba_Lab.ui.screens.RegistroScreen
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

@Composable
fun AppNavigation(temaViewModel: TemaViewModel) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val context = LocalContext.current.applicationContext
    //Viewmodel compartido conectado a Room
    val usuarioViewModel: UsuarioViewModel =  viewModel(factory= viewModelFactory {
        initializer {
            val database = AlbaDataBase.obtenerDatabase(context)
            val repository = SolicitudRepository(database.solicitudDao())
            UsuarioViewModel(repository)}
    })

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
                NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    // faltan agregar un par de ventanas
    AlbaScaffold(mainViewModel) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(mainViewModel) }
            composable(Screen.Profile.route) { ProfileScreen(mainViewModel) }
            composable(Screen.Settings.route) { SettingsScreen(mainViewModel, temaViewModel) }
            composable(Screen.Registro.route) { RegistroScreen(navController, usuarioViewModel) }
            composable(Screen.Resumen.route) { ResumenScreen(navController, usuarioViewModel) }
            composable(Screen.Catalogo.route) { CatalogoScreen(mainViewModel) }
            composable(Screen.Solicitudes.route){SolicitudesScreen(navController,usuarioViewModel)}
        }
    }
}
