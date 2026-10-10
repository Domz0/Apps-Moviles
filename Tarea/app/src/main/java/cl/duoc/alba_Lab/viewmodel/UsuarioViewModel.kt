package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.alba_Lab.model.UsuarioErrores
import cl.duoc.alba_Lab.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ViewModel compartido: mantiene los datos entre Registro y Resumen.
class UsuarioViewModel : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado = _estado.asStateFlow()

    // Nombre: actualiza el texto y limpia su error al editar.
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // Correo: actualiza el texto y limpia su error al editar.
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // Clave: guarda la contraseña ingresada en el estado del formulario.
    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    // Dirección: actualiza el texto y limpia su error al editar.
    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    // Checkbox: registra si el usuario acepta los términos.
    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    // Validación de la guía: campos obligatorios, correo con @ y clave de al menos 6 caracteres.
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre, errores.correo, errores.clave, errores.direccion
        ).isNotEmpty()
        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }
}
