package cl.duoc.alba_Lab.navigation

// Rutas: identificadores reutilizables de cada pantalla de la aplicación.
sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object Profile : Screen("profile_page")
    data object Settings : Screen("settings_page")
    data object Registro : Screen("registro")
    data object Resumen : Screen("resumen")
    data object Catalogo : Screen("catalogo")

    data object Solicitudes : Screen("solicitudes")
}
