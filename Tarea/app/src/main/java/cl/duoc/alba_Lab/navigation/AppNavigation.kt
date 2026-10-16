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
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    // Se crea una sola vez, fuera de las rutas, para compartir el formulario.
    val usuarioViewModel: UsuarioViewModel = viewModel()

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
            composable(Screen.Settings.route) { SettingsScreen(mainViewModel) }
            composable(Screen.Registro.route) { RegistroScreen(navController, usuarioViewModel) }
            composable(Screen.Resumen.route) { ResumenScreen(navController, usuarioViewModel) }
            composable(Screen.Catalogo.route) { CatalogoScreen(mainViewModel) }

        }
    }
}
