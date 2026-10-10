package cl.duoc.alba_Lab.navigation

// Rutas reutilizables para las tres pantallas de la aplicación.
sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object Profile : Screen("profile_page")
    data object Settings : Screen("settings_page")
}