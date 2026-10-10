package cl.duoc.alba_Lab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.alba_Lab.ui.screens.HomeScreen
import cl.duoc.alba_Lab.ui.screens.ProfileScreen
import cl.duoc.alba_Lab.ui.screens.RegistroScreen
import cl.duoc.alba_Lab.ui.screens.ResumenScreen
import cl.duoc.alba_Lab.ui.screens.SettingsScreen
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel

// Navegación: reúne las pantallas anteriores y el flujo Registro -> Resumen.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    // Se crea una sola vez, fuera de las rutas, para compartir el formulario.
    val usuarioViewModel: UsuarioViewModel = viewModel()

    // Eventos de la guía 10: permiten navegar desde el menú y la barra inferior.
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

    // Inicio conserva el logo; el menú da acceso al registro de la guía 11.
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(mainViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(mainViewModel) }
        composable(Screen.Settings.route) { SettingsScreen(mainViewModel) }
        composable(Screen.Registro.route) { RegistroScreen(navController, usuarioViewModel) }
        composable(Screen.Resumen.route) { ResumenScreen(navController, usuarioViewModel) }
    }
}
