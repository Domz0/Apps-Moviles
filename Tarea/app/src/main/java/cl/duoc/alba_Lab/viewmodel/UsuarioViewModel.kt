package cl.duoc.alba_Lab.viewmodel

import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.data.ImagenStorage
import cl.duoc.alba_Lab.data.SolicitudRepository
import cl.duoc.alba_Lab.data.CuentaRepository
import cl.duoc.alba_Lab.model.*
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray

// Estado compartido, con cuentas y solicitudes separadas. La sesión sigue siendo local y de prueba.
class UsuarioViewModel(
    private val repository: SolicitudRepository,
    private val cuentaRepository: CuentaRepository,
    private val fotos: ImagenStorage
) : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado = _estado.asStateFlow()
    private val _registro = MutableStateFlow(UsuarioUiState())
    val registro = _registro.asStateFlow()
    private val _sesion = MutableStateFlow<Cuenta?>(null)
    val sesion = _sesion.asStateFlow()
    private val _perfil = MutableStateFlow(PerfilUiState())
    val perfil = _perfil.asStateFlow()
    val solicitudes = repository.solicitudes.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun solicitudesDeCuenta(lista: List<Solicitud>, cuenta: Cuenta?): List<Solicitud> {
        if (cuenta == null) return emptyList()
        return lista.filter { it.cuentaId == cuenta.id }
    }

    private val clavesTemporales = mutableMapOf<String, String>()
    private var idSolicitudActual: String? = null
    private var cuentaSolicitudActual: String? = null
    private var fechaSolicitudActual: Long? = null
    private var imagenesOriginales = emptyList<String>()
    private var versionFormulario = 0

    fun actualizarRegistro(cambio: (UsuarioUiState) -> UsuarioUiState) {
        _registro.update { cambio(it).copy(errores = UsuarioErrores(), aviso = null) }
    }

    private fun erroresPersonales(datos: UsuarioUiState, conClave: Boolean) = UsuarioErrores(
        nombre = if (datos.nombre.isBlank()) "Ingresa tu nombre" else null,
        correo = if (!Patterns.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches()) "Ingresa un correo válido" else null,
        clave = if (conClave && datos.clave.isBlank()) "Ingresa tu contraseña" else null,
        direccion = if (conClave && datos.direccion.isBlank()) "Ingresa tu dirección" else null,
        aceptaTerminos = if (!datos.aceptaTerminos) "Debes aceptar los términos y condiciones" else null,
        tipoColaborador = if (datos.tipoColaborador !in listOf("Persona", "Empresa")) "Selecciona Persona o Empresa" else null,
        empresa = if (datos.tipoColaborador == "Empresa" && datos.empresa.isBlank()) "Ingresa el nombre de la empresa" else null
    )
    private fun UsuarioErrores.tieneErrores() =
        listOfNotNull(nombre, correo, clave, direccion, aceptaTerminos, tipoColaborador, empresa, modalidad).isNotEmpty()
    private fun correoNormalizado(correo: String) = correo.trim().lowercase(Locale.ROOT)

    suspend fun registrarCuenta(): Boolean {
        if (_sesion.value != null || _registro.value.procesando) return false
        val datos = _registro.value
        val errores = erroresPersonales(datos, conClave = true)
        _registro.update { it.copy(errores = errores, aviso = null) }
        if (errores.tieneErrores()) return false
        _registro.update { it.copy(procesando = true) }
        return try {
            val correo = correoNormalizado(datos.correo)
            if (cuentaRepository.buscarCuenta(correo) != null) {
                _registro.update { it.copy(errores = it.errores.copy(correo = "Este correo ya está registrado")) }
                false
            } else {
                val cuenta = Cuenta(UUID.randomUUID().toString(), datos.nombre.trim(), correo,
                    datos.tipoColaborador, if (datos.tipoColaborador == "Empresa") datos.empresa.trim() else "",
                    direccion = datos.direccion.trim(), aceptaTerminos = datos.aceptaTerminos)
                cuentaRepository.crearCuenta(cuenta)
                clavesTemporales[cuenta.id] = datos.clave
                abrirSesion(cuenta, datos.clave)
                _registro.value = UsuarioUiState()
                true
            }
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) {
            _registro.update { it.copy(aviso = "No se pudo crear la cuenta. Intenta nuevamente.") }
            false
        } finally { _registro.update { it.copy(procesando = false) } }
    }

    suspend fun iniciarSesionSimulada(correo: String, clave: String): Boolean {
        if (clave.isBlank()) return false
        val cuenta = cuentaRepository.buscarCuenta(correoNormalizado(correo)) ?: return false
        val conocida = clavesTemporales[cuenta.id]
        if (conocida != null && conocida != clave) return false
        // Igual que en el prototipo anterior, las contraseñas solo viven durante el proceso.
        clavesTemporales[cuenta.id] = clave
        abrirSesion(cuenta, clave)
        return true
    }
    private fun abrirSesion(cuenta: Cuenta, clave: String) {
        nuevaSolicitud()
        _sesion.value = cuenta
        _perfil.value = PerfilUiState(cuenta.id, cuenta.nombre, cuenta.correo, clave,
            tipoCuenta = cuenta.tipo, empresa = cuenta.empresa)
    }
    fun cerrarSesion() {
        nuevaSolicitud()
        _sesion.value = null
        _perfil.value = PerfilUiState()
        _registro.value = UsuarioUiState()
    }
    // Cada edición comienza con los datos guardados, sin arrastrar cambios descartados.
    fun prepararEdicionPerfil() {
        val cuenta = _sesion.value ?: return
        _perfil.value = PerfilUiState(
            id = cuenta.id, nombre = cuenta.nombre, correo = cuenta.correo,
            clave = clavesTemporales[cuenta.id].orEmpty(),
            tipoCuenta = cuenta.tipo, empresa = cuenta.empresa,
            revisionFoto = _perfil.value.revisionFoto
        )
    }

    fun onPerfilNombreChange(valor: String) { _perfil.update { it.copy(nombre = valor, errorNombre = null, mensaje = null) } }
    fun onPerfilEmpresaChange(valor: String) { _perfil.update { it.copy(empresa = valor, errorEmpresa = null, mensaje = null) } }
    fun onPerfilCorreoChange(valor: String) { _perfil.update { it.copy(correo = valor, errorCorreo = null, mensaje = null) } }
    fun onPerfilClaveChange(valor: String) { _perfil.update { it.copy(clave = valor, errorClave = null, mensaje = null) } }
    fun informarFoto(mensaje: String, actualizada: Boolean = false) {
        _perfil.update { it.copy(mensaje = mensaje, revisionFoto = it.revisionFoto + if (actualizada) 1 else 0) }
    }
    fun guardarPerfil() {
        val cuenta = _sesion.value ?: return
        val datos = _perfil.value
        if (datos.guardando) return
        val errorNombre = if (datos.nombre.isBlank()) "Ingresa tu nombre" else null
        val errorCorreo = if (!Patterns.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches()) "Ingresa un correo válido" else null
        val errorClave = if (datos.clave.isBlank()) "Ingresa tu contraseña" else null
        val errorEmpresa = if (cuenta.tipo == "Empresa" && datos.empresa.isBlank()) "Ingresa el nombre de la empresa" else null
        _perfil.update { it.copy(errorNombre = errorNombre, errorCorreo = errorCorreo, errorClave = errorClave,
            errorEmpresa = errorEmpresa, mensaje = null) }
        if (errorNombre != null || errorCorreo != null || errorClave != null || errorEmpresa != null) return
        _perfil.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                val correo = correoNormalizado(datos.correo)
                val otraCuenta = cuentaRepository.buscarCuenta(correo)
                if (otraCuenta != null && otraCuenta.id != cuenta.id) {
                    _perfil.update { it.copy(errorCorreo = "Este correo ya está registrado") }
                } else {
                    val actualizada = cuenta.copy(nombre = datos.nombre.trim(), correo = correo,
                        empresa = if (cuenta.tipo == "Empresa") datos.empresa.trim() else "")
                    cuentaRepository.actualizarCuenta(actualizada)
                    if (_sesion.value?.id == cuenta.id) {
                        _sesion.value = actualizada
                        clavesTemporales[cuenta.id] = datos.clave
                        _perfil.update { it.copy(nombre = actualizada.nombre, correo = correo,
                            empresa = actualizada.empresa, mensaje = "Datos guardados.") }
                    }
                }
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { _perfil.update { it.copy(mensaje = "No se pudieron guardar los datos.") }
            } finally { _perfil.update { it.copy(guardando = false) } }
        }
    }

    fun onNombreChange(valor: String) { _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) } }
    fun onCorreoChange(valor: String) { _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) } }
    fun onTipoColaboradorChange(valor: String) {
        _estado.update { it.copy(tipoColaborador = valor, empresa = if (valor == "Persona") "" else it.empresa,
            errores = it.errores.copy(tipoColaborador = null, empresa = null)) }
    }
    fun onEmpresaChange(valor: String) { _estado.update { it.copy(empresa = valor, errores = it.errores.copy(empresa = null)) } }
    fun onModalidadChange(valor: String) { _estado.update { it.copy(modalidad = valor, errores = it.errores.copy(modalidad = null)) } }
    fun onMensajeChange(valor: String) { _estado.update { it.copy(mensaje = valor) } }
    fun onTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null)) }
    }
    fun informarSolicitud(mensaje: String) { _estado.update { it.copy(aviso = mensaje) } }

    fun nuevaSolicitud(invitado: Boolean = false) {
        versionFormulario++
        val sobrantes = _estado.value.imagenes - imagenesOriginales.toSet()
        viewModelScope.launch { sobrantes.forEach { fotos.eliminar(it) } }
        idSolicitudActual = null
        cuentaSolicitudActual = if (invitado) null else _sesion.value?.id
        fechaSolicitudActual = null
        imagenesOriginales = emptyList()
        _estado.value = UsuarioUiState(esInvitado = invitado)
    }
    fun validarFormulario(): Boolean {
        val datos = _estado.value
        if (!datos.esInvitado && (_sesion.value == null || _sesion.value?.id != cuentaSolicitudActual)) {
            informarSolicitud("Inicia sesión para crear una solicitud con tu cuenta.")
            return false
        }
        val personales = if (datos.esInvitado) erroresPersonales(datos, conClave = false) else UsuarioErrores()
        val errores = personales.copy(modalidad = if (datos.modalidad !in modalidades) "Selecciona una forma de colaborar" else null)
        _estado.update { it.copy(errores = errores, aviso = null) }
        return !errores.tieneErrores()
    }
    fun adjuntarImagenes(uris: List<Uri>) {
        if (_estado.value.procesando || uris.isEmpty()) return
        val version = versionFormulario
        _estado.update { it.copy(procesando = true, aviso = null) }
        viewModelScope.launch {
            try {
                for (uri in uris) {
                    if (version != versionFormulario) break
                    val id = UUID.randomUUID().toString()
                    fotos.guardar(id, uri)
                    if (version == versionFormulario) _estado.update { it.copy(imagenes = it.imagenes + id) }
                    else fotos.eliminar(id)
                }
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (version == versionFormulario) informarSolicitud("Alguna imagen no se pudo cargar. Puedes reintentar o continuar sin ella.")
            } finally { if (version == versionFormulario) _estado.update { it.copy(procesando = false) } }
        }
    }
    fun quitarImagen(id: String) {
        if (_estado.value.procesando) return
        _estado.update { it.copy(imagenes = it.imagenes - id) }
        if (id !in imagenesOriginales) viewModelScope.launch { fotos.eliminar(id) }
    }
    suspend fun guardarSolicitud(): Boolean {
        if (_estado.value.procesando || !validarFormulario()) return false
        val datos = _estado.value
        val cuenta = if (datos.esInvitado) null else _sesion.value
        _estado.update { it.copy(procesando = true) }
        return try {
            val id = idSolicitudActual ?: UUID.randomUUID().toString()
            val solicitud = Solicitud(id, cuenta?.nombre ?: datos.nombre.trim(),
                cuenta?.correo ?: correoNormalizado(datos.correo), cuenta?.direccion.orEmpty(), cuenta?.tipo ?: datos.tipoColaborador,
                cuenta?.empresa ?: if (datos.tipoColaborador == "Empresa") datos.empresa.trim() else "",
                datos.modalidad, datos.mensaje.trim(), cuenta?.aceptaTerminos ?: datos.aceptaTerminos,
                if (idSolicitudActual == null) System.currentTimeMillis() else fechaSolicitudActual,
                cuenta?.id, JSONArray(datos.imagenes).toString())
            repository.guardarSolicitud(solicitud)
            val sobrantes = imagenesOriginales - datos.imagenes.toSet()
            idSolicitudActual = id
            fechaSolicitudActual = solicitud.fechaCreacion
            imagenesOriginales = datos.imagenes
            sobrantes.forEach { fotos.eliminar(it) }
            true
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) {
            informarSolicitud("No se pudo guardar la solicitud. Intenta nuevamente.")
            false
        } finally { _estado.update { it.copy(procesando = false) } }
    }
    fun editarSolicitud(id: String): Boolean {
        val cuenta = _sesion.value ?: return false
        val solicitud = solicitudes.value.find { it.id == id && it.cuentaId == cuenta.id } ?: return false
        nuevaSolicitud()
        idSolicitudActual = solicitud.id
        cuentaSolicitudActual = cuenta.id
        fechaSolicitudActual = solicitud.fechaCreacion
        imagenesOriginales = imagenesDe(solicitud)
        _estado.value = UsuarioUiState(modalidad = solicitud.modalidad, mensaje = solicitud.mensaje, imagenes = imagenesOriginales)
        return true
    }
    fun eliminarSolicitud(id: String) {
        val cuenta = _sesion.value ?: return
        viewModelScope.launch {
            val solicitud = repository.buscarSolicitud(id) ?: return@launch
            if (solicitud.cuentaId != cuenta.id) return@launch
            repository.eliminarSolicitud(id)
            imagenesDe(solicitud).forEach { fotos.eliminar(it) }
            if (idSolicitudActual == id) nuevaSolicitud()
        }
    }
    companion object {
        val modalidades = listOf("Donación", "Voluntariado", "Alianza empresarial", "Consulta por productos y servicios")
        fun imagenesDe(solicitud: Solicitud): List<String> {
            val json = JSONArray(solicitud.imagenes)
            return List(json.length()) { json.getString(it) }
        }
    }
}
