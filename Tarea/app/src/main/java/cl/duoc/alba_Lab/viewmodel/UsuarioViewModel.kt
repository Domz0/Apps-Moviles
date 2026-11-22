package cl.duoc.alba_Lab.viewmodel

import android.net.Uri
import androidx.core.util.PatternsCompat
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
import cl.duoc.alba_Lab.data.AlbaLabApi
import cl.duoc.alba_Lab.data.CatalogoWebRepository
import cl.duoc.alba_Lab.data.CuentasWebRepository
import retrofit2.HttpException
import cl.duoc.alba_Lab.data.SolicitudCuentaWeb

// Estado compartido, con cuentas y solicitudes separadas. La sesión sigue siendo local y de prueba.
class UsuarioViewModel(
    private val repository: SolicitudRepository,
    private val cuentaRepository: CuentaRepository,
    private val fotos: ImagenStorage,
    private val repositorioWeb: CatalogoWebRepository = CatalogoWebRepository(AlbaLabApi.catalogo),
    private val cuentasWeb: CuentasWebRepository = CuentasWebRepository()
) : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado = _estado.asStateFlow()
    private val _registro = MutableStateFlow(UsuarioUiState())
    val registro = _registro.asStateFlow()
    private val _sesion = MutableStateFlow<Cuenta?>(null)
    val sesion = _sesion.asStateFlow()
    private val _perfil = MutableStateFlow(PerfilUiState())
    val perfil = _perfil.asStateFlow()
    private val _solicitudesWeb = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudes = _solicitudesWeb.asStateFlow()

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
        correo = if (!PatternsCompat.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches()) "Ingresa un correo válido" else null,
        clave = if (conClave && datos.clave.isBlank()) "Ingresa tu contraseña" else null,
        direccion = if (conClave && datos.direccion.isBlank()) "Ingresa tu dirección" else null,
        aceptaTerminos = if (!datos.aceptaTerminos) "Debes aceptar los términos y condiciones" else null,
        tipoColaborador = if (datos.tipoColaborador !in listOf("Persona", "Empresa")) "Selecciona Persona o Empresa" else null,
        empresa = if (datos.tipoColaborador == "Empresa" && datos.empresa.isBlank()) "Ingresa el nombre de la empresa" else null
    )
    private fun UsuarioErrores.tieneErrores() =
        listOfNotNull(nombre, correo, clave, direccion, aceptaTerminos, tipoColaborador, empresa, modalidad).isNotEmpty()
    private fun correoNormalizado(correo: String) = correo.trim().lowercase(Locale.ROOT)

    suspend fun registrarCuenta(): Boolean{
        if (_sesion.value != null || _registro.value.procesando) {return false}
        val datos = _registro.value
        val errores = erroresPersonales(datos, conClave = true)

        _registro.update {it.copy(errores = errores, aviso = null)}
        if (errores.tieneErrores()) return false
        _registro.update {it.copy(procesando = true)}

        var creadaEnServidor = false
        return try {
            val correo = correoNormalizado(datos.correo)
            //crea la cuenta y guarda la contraseña como hash en el server
            cuentasWeb.registrarCuenta( nombre = datos.nombre, correo = correo, clave = datos.clave)
            creadaEnServidor = true

            //comprueba el acceso antes de abrir la sesión en androide
            val remota = cuentasWeb.iniciarSesion(correo, datos.clave)
            val existente = cuentaRepository.buscarCuenta(correo)

            val cuenta = Cuenta(
                id = existente?.id ?: UUID.randomUUID().toString(),
                nombre = remota.nombre,
                correo = remota.correo,
                tipo = datos.tipoColaborador,
                empresa = if (datos.tipoColaborador == "Empresa") {datos.empresa.trim()} else {""},
                direccion = datos.direccion.trim(),
                aceptaTerminos = datos.aceptaTerminos)

            if (existente == null) {cuentaRepository.crearCuenta(cuenta)
            } else {cuentaRepository.actualizarCuenta(cuenta)}

            clavesTemporales[cuenta.id] = datos.clave
            abrirSesion(cuenta, datos.clave)
            cargarSolicitudes()
            _registro.value = UsuarioUiState()
            true
        } catch (e: CancellationException) {
            cuentasWeb.cerrarSesion()
            throw e
        } catch (e: HttpException) {
            cuentasWeb.cerrarSesion()
            if (creadaEnServidor) {
                _registro.update {it.copy(aviso = "La cuenta se creó. Intenta iniciar sesión.")}
            } else if (e.code() == 409) {
                _registro.update {it.copy(errores = it.errores.copy(correo = "Este correo ya está registrado"))
                }
            } else {
                _registro.update {it.copy(aviso = "No se pudo completar el registro. Revisa los datos y usa una contraseña de al menos 6 caracteres.")}
            }
            false

        } catch (e: Exception) {
            cuentasWeb.cerrarSesion()
            _registro.update {
                it.copy(aviso = if (creadaEnServidor) {"La cuenta se creó. Intenta iniciar sesión."
                    } else {"No se pudo conectar con el servidor. Intenta nuevamente."})
            }
            false
        } finally {_registro.update {it.copy(procesando = false)}}
    }

    suspend fun iniciarSesion(correo: String, clave: String): Boolean {
        if (clave.isBlank() || _sesion.value != null) return false
        return try {
            //la contraseña se comprueba en el server
            val remota = cuentasWeb.iniciarSesion(correo, clave)
            val correoCuenta = correoNormalizado(remota.correo)

            //conserva los datos locales como el id usado por el carritox
            val existente = cuentaRepository.buscarCuenta(correoCuenta)
            val cuenta = if (existente != null) {val actualizada = existente.copy(nombre = remota.nombre,correo = correoCuenta)
                cuentaRepository.actualizarCuenta(actualizada)
                actualizada
            } else {
                val nueva = Cuenta(id = UUID.randomUUID().toString(), nombre = remota.nombre, correo = correoCuenta, tipo = "Persona", empresa = "",
                    direccion = "", aceptaTerminos = false)
                cuentaRepository.crearCuenta(nueva)
                nueva
            }
            clavesTemporales[cuenta.id] = clave
            abrirSesion(cuenta, clave)
            true
        } catch (e: HttpException) {cuentasWeb.cerrarSesion()
            if (e.code() == 401) {false
            } else {throw e}
        } catch (e: Exception) {
            cuentasWeb.cerrarSesion()
            throw e
        }
    }
    private fun abrirSesion(cuenta: Cuenta, clave: String) {
        nuevaSolicitud()
        _sesion.value = cuenta
        _perfil.value = PerfilUiState(cuenta.id, cuenta.nombre, cuenta.correo, clave,
            tipoCuenta = cuenta.tipo, empresa = cuenta.empresa)
    }
    fun cerrarSesion() {
        cuentasWeb.cerrarSesion()
        clavesTemporales.clear()
        nuevaSolicitud()
        _sesion.value= null
        _perfil.value= PerfilUiState()
        _registro.value= UsuarioUiState()
        _solicitudesWeb.value= emptyList()
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

        val errorNombre = if (datos.nombre.isBlank()) {"Ingresa tu nombre"} else null

        val errorCorreo = if (!PatternsCompat.EMAIL_ADDRESS.matcher(datos.correo.trim()).matches()) {"Ingresa un correo válido"
        } else null

        val errorClave = if (datos.clave.length < 6) {"La contraseña debe tener al menos 6 caracteres"} else null

        val errorEmpresa = if (cuenta.tipo == "Empresa" && datos.empresa.isBlank()
        ) {"Ingresa el nombre de la empresa"
        } else null

        _perfil.update {it.copy(errorNombre = errorNombre, errorCorreo = errorCorreo, errorClave = errorClave,
                errorEmpresa = errorEmpresa, mensaje = null)
        }
        if (errorNombre != null || errorCorreo != null || errorClave != null || errorEmpresa != null) {
            return
        }

        _perfil.update {it.copy(guardando = true)}
        viewModelScope.launch {
            try {
                val correo = correoNormalizado(datos.correo)
                val otraCuenta = cuentaRepository.buscarCuenta(correo)

                if (otraCuenta != null && otraCuenta.id != cuenta.id) {
                    _perfil.update {it.copy(errorCorreo = "Este correo ya está registrado")}
                    return@launch
                }
                //primero guarda nombre, correo y contraseña en postre
                val remota = cuentasWeb.actualizarPerfil(nombre = datos.nombre, correo = correo, clave = datos.clave)
                if (_sesion.value?.id != cuenta.id) return@launch
                val actualizada = cuenta.copy(nombre = remota.nombre, correo = remota.correo, empresa = if (cuenta.tipo == "Empresa") {datos.empresa.trim()
                    } else {
                        ""
                    }
                )
                _sesion.value = actualizada
                clavesTemporales[cuenta.id] = datos.clave

                _perfil.update {it.copy(nombre = actualizada.nombre, correo = actualizada.correo, empresa = actualizada.empresa, mensaje = "Datos guardados.")}
                //actualiza los datos mostrados en el historial
                _solicitudesWeb.update { lista -> lista.map { solicitud ->
                        solicitud.copy(nombre = actualizada.nombre, correo = actualizada.correo, empresa = actualizada.empresa)}
                }

                //mantiene el mismo id local para mantener carrito y foto
                try {
                    cuentaRepository.actualizarCuenta(actualizada)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {_perfil.update {it.copy(mensaje = "Perfil guardado en el servidor. No se pudo actualizar la copia local.")
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (_sesion.value?.id == cuenta.id) {_perfil.update {
                        when (e.code()) {
                            409 -> it.copy(errorCorreo = "Este correo ya está registrado")
                            401 -> it.copy(mensaje = "Vuelve a iniciar sesión para actualizar el perfil.")
                            400 -> it.copy(mensaje = "Revisa los datos: nombre y correo hasta 120 caracteres, contraseña de al menos 6 caracteres y hasta 72 bytes.")
                            else -> it.copy(mensaje = "No se pudieron guardar los cambios.")
                        }
                    }
                }
            } catch (e: Exception) {
                if (_sesion.value?.id == cuenta.id) {
                    _perfil.update {it.copy(mensaje = "No se pudo confirmar el guardado. Revisa la conexión.")}
                }
            } finally {
                if (_sesion.value?.id == cuenta.id) {_perfil.update { it.copy(guardando = false)}
                }
            }
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
        val cuenta = _sesion.value
        val mensaje = datos.mensaje.trim()

        if (mensaje.isEmpty() || mensaje.length > 1000) {
            informarSolicitud("Escribe un mensaje de entre 1 y 1000 caracteres.")
            return false
        }

        if (!datos.esInvitado && cuenta == null) return false

        val idActual = idSolicitudActual
        val idRemoto = idActual
            ?.takeIf { it.startsWith("api_") }
            ?.removePrefix("api_")
            ?.toLongOrNull()

        if (!datos.esInvitado && idActual != null && idRemoto == null) {
            informarSolicitud("Esta solicitud antigua solo existe en el teléfono.")
            return false
        }

        _estado.update { it.copy(procesando = true, aviso = null) }

        return try {
            if (datos.esInvitado) {
                val enviado = repositorioWeb.enviarSolicitudInvitado(datos.modalidad, mensaje)
                if (!enviado) {informarSolicitud("No se pudo enviar la solicitud.")}
                return enviado
            }

            val cuentaActual = cuenta ?: return false
            val remota = cuentasWeb.guardarSolicitud(id = idRemoto, modalidad = datos.modalidad, mensaje = mensaje)

            //la solicitud ya se guardó en el servidor
            if (_sesion.value?.id != cuentaActual.id) return true

            val idLocal = "api_${remota.id}"
            val fecha = fechaSolicitudActual ?: System.currentTimeMillis()
            val solicitud = Solicitud(idLocal, cuentaActual.nombre, cuentaActual.correo, cuentaActual.direccion, cuentaActual.tipo,
                cuentaActual.empresa, remota.modalidad, remota.mensaje, cuentaActual.aceptaTerminos, fecha, cuentaActual.id,
                JSONArray(datos.imagenes).toString())

            val sobrantes = imagenesOriginales - datos.imagenes.toSet()

            idSolicitudActual = idLocal
            fechaSolicitudActual = fecha
            imagenesOriginales = datos.imagenes

            _solicitudesWeb.update { lista -> listOf(solicitud) + lista.filterNot {it.id == idLocal}}

            //roomcito conserva una copia con la fecha y las imágenes locales
            //si hay un fallo de esta copia no invalida el guardado en postgre
            try {
                repository.guardarSolicitud(solicitud)
                sobrantes.forEach { fotos.eliminar(it) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                informarSolicitud("Solicitud enviada. No se pudo actualizar la copia local.")
            }
            true

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {informarSolicitud("No se pudo guardar la solicitud. Intenta nuevamente.")
            false
        } finally {_estado.update { it.copy(procesando = false) }}
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

        val solicitud = solicitudes.value.find {it.id == id && it.cuentaId == cuenta.id} ?: return
        val idRemoto = id
            .takeIf { it.startsWith("api_") }
            ?.removePrefix("api_")
            ?.toLongOrNull() ?: return

        viewModelScope.launch {
            try {
                //primero elimina en postgre
                cuentasWeb.eliminarSolicitud(idRemoto)

                if (_sesion.value?.id != cuenta.id) return@launch

                _solicitudesWeb.update { lista ->lista.filterNot {it.id == id }}
                if (idSolicitudActual == id) nuevaSolicitud()

                //Luego limpia la copia del telef
                try {
                    repository.eliminarSolicitud(id)
                    imagenesDe(solicitud).forEach { fotos.eliminar(it) }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    informarSolicitud("Solicitud eliminada del servidor. No se pudo limpiar la copia local."
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {informarSolicitud("No se pudo eliminar la solicitud.")
            }
        }
    }
    //consulta las solicitudes que pertenecen a la cuenta autenticada
    fun cargarSolicitudes() {val cuenta = _sesion.value ?: return
        viewModelScope.launch {
            try {
                val remotas = cuentasWeb.obtenerSolicitudes()
                val lista = remotas.map { convertirSolicitud(it, cuenta) }

                if (_sesion.value?.id == cuenta.id) {_solicitudesWeb.value = lista}
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (_sesion.value?.id == cuenta.id) {informarSolicitud("No se pudieron actualizar tus solicitudes. Intenta nuevamente.")}
            }
        }
    }

    //adapta la respuesta de la api al modelo que ya usa la pantalla
    private suspend fun convertirSolicitud(remota: SolicitudCuentaWeb, cuenta: Cuenta
    ): Solicitud {
        val idLocal = "api_${remota.id}"
        val guardada = repository.buscarSolicitud(idLocal)

        return Solicitud(idLocal, cuenta.nombre, cuenta.correo, cuenta.direccion, cuenta.tipo, cuenta.empresa,
            remota.modalidad, remota.mensaje, cuenta.aceptaTerminos, guardada?.fechaCreacion, cuenta.id,
            if (guardada?.cuentaId == cuenta.id) {guardada.imagenes
            } else { "[]" }
        )
    }
    companion object {
        val modalidades = listOf("Donación", "Voluntariado", "Alianza empresarial", "Consulta por productos y servicios")
        fun imagenesDe(solicitud: Solicitud): List<String> {
            val json = JSONArray(solicitud.imagenes)
            return List(json.length()) { json.getString(it) }
        }
    }
}
