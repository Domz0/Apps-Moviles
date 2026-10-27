package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.alba_Lab.model.UsuarioErrores
import cl.duoc.alba_Lab.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import cl.duoc.alba_Lab.model.Solicitud
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.data.SolicitudRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import cl.duoc.alba_Lab.model.PerfilUiState
import kotlinx.coroutines.CancellationException

// ViewModel compartido: mantiene los datos entre Registro y Resumen.
class UsuarioViewModel(private val repository: SolicitudRepository) : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado = _estado.asStateFlow()
    //Lista de solicitudes obtenidas desde Room
    val solicitudes = repository.solicitudes.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList())

    //Aqui se identifica la solicitud que estamos creando o editando
    private var idSolicitudActual: String? = null
    private val _perfil = MutableStateFlow(PerfilUiState())
    val perfil = _perfil.asStateFlow()
    private var perfilCargado = false
    // Las claves son temporales: la tabla existente no almacena contraseñas.
    private val clavesTemporales = mutableMapOf<String, String>()

    fun cargarPerfil() {
        if (perfilCargado) return
        val actual = _estado.value
        val registro = solicitudes.value.firstOrNull { it.id == idSolicitudActual }
            ?: solicitudes.value.firstOrNull()
        if (actual.nombre.isNotBlank() && actual.correo.isNotBlank()) {
            _perfil.value = PerfilUiState(idSolicitudActual, actual.nombre, actual.correo, actual.clave)
            perfilCargado = true
        } else if (registro != null) {
            _perfil.value = PerfilUiState(registro.id, registro.nombre, registro.correo,
                clavesTemporales[registro.id].orEmpty())
            perfilCargado = true
        }
    }

    // Simulación: busca el correo registrado. Si existe una clave en memoria, la comprueba.
    // Los registros antiguos no tienen clave guardada; usan la ingresada para esta sesión.
    fun iniciarSesionSimulada(correo: String, clave: String): Boolean {
        val registro = solicitudes.value.firstOrNull { it.correo.equals(correo.trim(), true) }
        val actual = _estado.value
        val usaActual = actual.correo.trim().equals(correo.trim(), true) && actual.nombre.isNotBlank()
        if (registro == null && !usaActual) return false
        val id = registro?.id ?: idSolicitudActual
        val conocida = id?.let { clavesTemporales[it] }
            ?: if (usaActual) actual.clave else ""
        if (!conocida.isNullOrBlank() && conocida != clave) return false
        _perfil.value = PerfilUiState(id, registro?.nombre ?: actual.nombre,
            registro?.correo ?: actual.correo.trim(), clave)
        if (id != null) clavesTemporales[id] = clave
        perfilCargado = true
        return true
    }

    fun onPerfilNombreChange(valor: String) {
        _perfil.update { it.copy(nombre = valor, errorNombre = null, mensaje = null) }
    }
    fun onPerfilCorreoChange(valor: String) {
        _perfil.update { it.copy(correo = valor, errorCorreo = null, mensaje = null) }
    }
    fun onPerfilClaveChange(valor: String) {
        _perfil.update { it.copy(clave = valor, errorClave = null, mensaje = null) }
    }
    fun informarFoto(mensaje: String, actualizada: Boolean = false) {
        _perfil.update { it.copy(mensaje = mensaje,
            revisionFoto = it.revisionFoto + if (actualizada) 1 else 0) }
    }

    fun guardarPerfil() {
        if (_perfil.value.guardando) return
        val datos = _perfil.value
        val errorNombre = if (datos.nombre.isBlank()) "Ingresa tu nombre" else null
        val errorCorreo = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches())
            "Ingresa un correo válido" else null
        val errorClave = if (datos.clave.isBlank()) "Ingresa tu contraseña" else null
        _perfil.update { it.copy(errorNombre = errorNombre, errorCorreo = errorCorreo,
            errorClave = errorClave, mensaje = null) }
        if (errorNombre != null || errorCorreo != null || errorClave != null) return
        _perfil.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                if (datos.id != null) {
                    check(repository.actualizarDatosPersonales(datos.id, datos.nombre.trim(), datos.correo.trim()) > 0)
                    clavesTemporales[datos.id] = datos.clave
                }
                if (datos.id == idSolicitudActual) {
                    _estado.update { it.copy(nombre = datos.nombre.trim(), correo = datos.correo.trim(), clave = datos.clave) }
                }
                perfilCargado = true
                _perfil.update { it.copy(nombre = datos.nombre.trim(), correo = datos.correo.trim(),
                    guardando = false, mensaje = if (datos.id == null) "Datos actualizados en el formulario."
                    else "Nombre y correo guardados. Contraseña actualizada para esta sesión.") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _perfil.update { it.copy(guardando = false, mensaje = "No se pudieron guardar los datos. Intenta nuevamente.") }
            }
        }
    }

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

    //Actualizp si la colaboracion viene de empresa o persona
    fun onTipoColaboradorChange(valor: String) {
        _estado.update {
            it.copy(tipoColaborador = valor,
                    empresa= if (valor== "Persona") "" else it.empresa,
                    errores = it.errores.copy(
                        tipoColaborador= null,
                        empresa= null
                    )
            )
        }
    }
    //Actualizo el nombre de la empresa
    fun onEmpresaChange(valor: String) {
        _estado.update {
            it.copy(empresa = valor,
                    errores = it.errores.copy(empresa = null)
            )
        }
    }
    //Actualizo la modalidad de colabracion
    fun onModalidadChange(valor: String) {
        _estado.update {
            it.copy(modalidad = valor,
                    errores = it.errores.copy(modalidad = null))
        }
    }
    //Actualizamos el menaje del formulario
    fun onMensajeChange(valor: String) {
        _estado.update{
            it.copy(mensaje = valor,
                    errores = it.errores.copy(mensaje = null))
        }
    }
    //Limpio el formulario para nueva solicitud
    fun nuevaSolicitud(){
        idSolicitudActual= null
        _estado.value = UsuarioUiState()
    }
    //Se guarda una solicitud nueva o se actualiza la actual
    suspend fun guardarSolicitud(): Boolean{
        if(!validarFormulario()) {
            return false
        }
        val datos = _estado.value
        val id= idSolicitudActual ?: UUID.randomUUID().toString()
        val solicitud = Solicitud(
            id=id,
            nombre= datos.nombre.trim(),
            correo = datos.correo.trim(),
            direccion = datos.direccion.trim(),
            tipoColaborador = datos.tipoColaborador,
            empresa =if (datos.tipoColaborador== "Empresa"){datos.empresa.trim()
            }else {
            ""},
            modalidad = datos.modalidad,
            mensaje = datos.mensaje.trim(),
            aceptaTerminos = datos.aceptaTerminos)
        repository.guardarSolicitud(solicitud)
        idSolicitudActual = id
        clavesTemporales[id] = datos.clave
        if (!perfilCargado || _perfil.value.id == id || _perfil.value.id == null) {
            _perfil.value = PerfilUiState(id, solicitud.nombre, solicitud.correo, datos.clave)
            perfilCargado = true
        }
        return true
    }
    //Se carga una nueva solicitud ya guardada en formulario
    fun editarSolicitud(id: String): Boolean {
        val solicitud = solicitudes.value.find {it.id == id} ?: return false

        idSolicitudActual = solicitud.id

        _estado.value = UsuarioUiState(nombre = solicitud.nombre,correo = solicitud.correo,
            clave = clavesTemporales[id].orEmpty(),
            direccion = solicitud.direccion,tipoColaborador = solicitud.tipoColaborador,empresa = solicitud.empresa,
            modalidad = solicitud.modalidad,mensaje = solicitud.mensaje, aceptaTerminos = solicitud.aceptaTerminos)
        return true
    }
    //se elimina la solicitud que tenga ese identicadr
    fun eliminarSolicitud(id: String) {
        viewModelScope.launch{
            repository.eliminarSolicitud(id)
            clavesTemporales.remove(id)
            if (_perfil.value.id == id) {
                _perfil.value = PerfilUiState()
                perfilCargado = false
            }
            if (idSolicitudActual == id){
                nuevaSolicitud()
            }
        }
    }


    //valida los campos del formulario de colaboracion
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(estadoActual.correo.trim())
                    .matches()
                ) {
                "Ingresa un correo válido"
            }else null,
            tipoColaborador = if(estadoActual.tipoColaborador !in listOf("Persona", "Empresa")){
                "Selecciona persona o empresa"
            } else null,
            empresa = if (estadoActual.tipoColaborador == "Empresa" && estadoActual.empresa.isBlank()){
                "Ingresa el nombre de la empresa"
            } else null,
            modalidad = if(
                estadoActual.modalidad !in listOf("Donación", "Voluntariado", "Alianza empresarial",
                    "Consulta por productos y servicios")) {
                "Selecciona una modalidad de colaboración"
            }else null,
            mensaje = if (estadoActual.mensaje.isBlank()){"Escribenos cómo te gustaría colaborar"
            }else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.tipoColaborador,
            errores.empresa,
            errores.modalidad,
            errores.mensaje
        ).isNotEmpty()
        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }
}
