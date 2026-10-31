package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//definimos el tema default leyendo la configuracion del telefono
enum class ModoTema(val titulo: String) {
    SISTEMA("Sistema"),
    CLARO("Claro"),
    OSCURO("Oscuro")
}

class TemaViewModel : ViewModel() {
    private val _modo = MutableStateFlow(ModoTema.SISTEMA)
    val modo = _modo.asStateFlow()

    fun cambiarModo(modo: ModoTema) {
        _modo.value = modo
    }
}
