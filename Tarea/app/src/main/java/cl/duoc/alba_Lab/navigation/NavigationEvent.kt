package cl.duoc.alba_Lab.navigation

// Representa los distintos tipos de eventos de navegación.
sealed class NavigationEvent {
    // Navegar a un destino y, opcionalmente, limpiar parte de la pila.
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Volver a la pantalla anterior.
    data object PopBackStack : NavigationEvent()

}
