package cl.duoc.alba_Lab.navigation

// Rutas: identificadores reutilizables de cada pantalla de la aplicación.
sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object Login : Screen("login")
    data object Profile : Screen("profile_page")
    data object EditarPerfil : Screen("editar_perfil")
    data object Settings : Screen("settings_page")
    data object Registro : Screen("registro")
    data object CrearSolicitud : Screen("crear_solicitud")
    data object Invitado : Screen("colaborar_invitado")
    data object HistorialDebug : Screen("historial_debug")
    data object Resumen : Screen("resumen")
    data object Catalogo : Screen("catalogo")
    data object Carrito : Screen("carrito")
    data object QueHacemos: Screen("que_hacemos")
    data object Contacto : Screen("contacto")
    data object Solicitudes : Screen("solicitudes")
}
