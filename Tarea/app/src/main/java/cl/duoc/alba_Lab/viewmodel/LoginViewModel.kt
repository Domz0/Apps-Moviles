package cl.duoc.alba_Lab.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import cl.duoc.alba_Lab.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _estado = MutableStateFlow(LoginUiState())
    val estado = _estado.asStateFlow()

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errorCorreo = null, datosValidos = false) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errorClave = null, datosValidos = false) }
    }

    // Solo valida los campos; todavía no comprueba una cuenta ni crea una sesión.
    fun validarFormulario() {
        val datos = _estado.value
        val correo = datos.correo.trim()
        val errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo electrónico"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "Ingresa un correo válido"
            else -> null
        }
        val errorClave = if (datos.clave.isBlank()) "Ingresa tu contraseña" else null

        _estado.update {
            it.copy(
                correo = correo,
                errorCorreo = errorCorreo,
                errorClave = errorClave,
                datosValidos = errorCorreo == null && errorClave == null
            )
        }
    }
}
