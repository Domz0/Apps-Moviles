package cl.duoc.alba_Lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.alba_Lab.navigation.NavigationEvent
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.screens.HomeScreen
import cl.duoc.alba_Lab.ui.screens.ProfileScreen
import cl.duoc.alba_Lab.ui.screens.SettingsScreen
import cl.duoc.alba_Lab.ui.theme.AlbaTheme
import cl.duoc.alba_Lab.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlbaTheme {
                // ViewModel y controlador compartidos por las pantallas.
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                // Escuchar los eventos de navegación emitidos por el ViewModel.
                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { event ->
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

                // Grafo de navegación: Inicio, Perfil y Configuración.
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) { HomeScreen(viewModel) }
                    composable(Screen.Profile.route) { ProfileScreen(viewModel) }
                    composable(Screen.Settings.route) { SettingsScreen(viewModel) }
                }
            }
        }
    }
}