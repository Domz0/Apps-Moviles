package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.alba_Lab.model.UsuarioErrores
import cl.duoc.alba_Lab.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import cl.duoc.alba_Lab.model.Solicitud

// ViewModel compartido: mantiene los datos entre Registro y Resumen.
class UsuarioViewModel : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado = _estado.asStateFlow()
    //Lista temporal de solicitudes
    private val _solicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudes = _solicitudes.asStateFlow()

    //Aqui se identifica la solicitud que estamos creando o editando
    private var idSolicitudActual: String? = null

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
    fun guardarSolicitud():Boolean {
        if(!validarFormulario()){return false}

        val datos = _estado.value
        val id = idSolicitudActual ?: UUID.randomUUID().toString()

        val solicitud = Solicitud(id= id, nombre= datos.nombre.trim(), correo = datos.correo.trim(),
            direccion = datos.direccion.trim(), tipoColaborador = datos.tipoColaborador,
            empresa= if(datos.tipoColaborador== "Empresa"){datos.empresa.trim()
            }else{ "" },
            modalidad = datos.modalidad,
            mensaje= datos.mensaje.trim(),
            aceptaTerminos = datos.aceptaTerminos)

        _solicitudes.update { lista -> if (lista.any { it.id == id }) {lista.map { existente ->
                    if (existente.id == id) solicitud else existente}
            } else {lista + solicitud}}

        idSolicitudActual = id
        return true
    }
    //Se carga una nueva solicitud ya guardada en formulario
    fun editarSolicitud(id: String): Boolean {
        val solicitud = _solicitudes.value.find {it.id == id} ?: return false

        idSolicitudActual = solicitud.id

        _estado.value = UsuarioUiState(nombre = solicitud.nombre,correo = solicitud.correo,
            direccion = solicitud.direccion,tipoColaborador = solicitud.tipoColaborador,empresa = solicitud.empresa,
            modalidad = solicitud.modalidad,mensaje = solicitud.mensaje, aceptaTerminos = solicitud.aceptaTerminos)
        return true
    }
    //se elimina la solicitud que tenga ese identicadr
    fun eliminarSolicitud(id: String) {_solicitudes.update {lista -> lista.filterNot { it.id == id }}
        if (idSolicitudActual == id) {nuevaSolicitud()}
    }

    // Validación de la guía: campos obligatorios, correo con @ y clave de al menos 6 caracteres.
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
