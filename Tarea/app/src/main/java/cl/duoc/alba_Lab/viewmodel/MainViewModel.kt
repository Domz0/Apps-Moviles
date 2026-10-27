package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.navigation.NavigationEvent
import cl.duoc.alba_Lab.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    // Expone los eventos como un flujo de solo lectura para la interfaz.
    val navigationEvents = _navigationEvents.asSharedFlow()

    // Emitir un evento para navegar a la ruta deseada.
    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = screen,
                    popUpToRoute = if (screen == Screen.Home) Screen.Home else null,
                    singleTop = true
                )
            )
        }
    }

    // Volver a la pantalla anterior.
    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

}
