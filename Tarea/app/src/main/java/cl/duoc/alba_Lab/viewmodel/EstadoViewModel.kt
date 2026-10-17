package cl.duoc.alba_Lab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.data.EstadoDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EstadoViewModel(application: Application) : AndroidViewModel(application) {

    private val estadoDataStore = EstadoDataStore(application)

    //null indica que el estado todavia se está cargando
    private val _activo = MutableStateFlow<Boolean?>(null)
    val activo: StateFlow<Boolean?> = _activo

    // Controla la visibilidad del mensaje de confirmación.
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje

    init {cargarEstado()}
    //espera 1,5 segundos para simular la carga y recupera la preferencia
    fun cargarEstado() {viewModelScope.launch {delay(1500)
            _activo.value = estadoDataStore.obtenerEstado().first()}
    }

    //cambia entre activado y desactivado y nos guarda el resultado

    fun alternarEstado() {viewModelScope.launch {
            val nuevoValor = !(_activo.value ?: false)

            estadoDataStore.guardarEstado(nuevoValor)
            _activo.value = nuevoValor

            _mostrarMensaje.value = true
            delay(2000) //muestra la confirmacion durante 2 seg
            _mostrarMensaje.value = false
        }
    }
}