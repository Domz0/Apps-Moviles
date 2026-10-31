package cl.duoc.alba_Lab.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.data.ImagenStorage
import cl.duoc.alba_Lab.ui.components.FotoPerfil
import androidx.navigation.NavController
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun EditarPerfilScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.perfil.collectAsState()
    LaunchedEffect(Unit) { viewModel.prepararEdicionPerfil() }

    val context = LocalContext.current.applicationContext
    val fotos = remember(context) { ImagenStorage(context) }
    val scope = rememberCoroutineScope()
    var cargandoFoto by remember { mutableStateOf(false) }
    val foto by produceState<Bitmap?>(null, estado.id, estado.revisionFoto) {
        value = null
        estado.id?.let { value = fotos.cargar(it) }
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val id = estado.id
        if (uri != null && id != null) {
            scope.launch {
                cargandoFoto = true
                try {
                    fotos.guardar(id, uri)
                    viewModel.informarFoto("Foto guardada.", actualizada = true)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    viewModel.informarFoto("No se pudo cargar la imagen. Elige otra foto.")
                } finally {
                    cargandoFoto = false
                }
            }
        }
    }

    Column(
        Modifier.fillMaxSize().wrapContentWidth().widthIn(max = 600.dp).fillMaxWidth().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Editar perfil", style = MaterialTheme.typography.headlineMedium)
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            FotoPerfil(foto)
            if (cargandoFoto) CircularProgressIndicator()
            TextButton(
                onClick = {
                    try {
                        galeria.launch("image/*")
                    } catch (e: android.content.ActivityNotFoundException) {
                        viewModel.informarFoto("No hay una aplicación disponible para seleccionar imágenes.")
                    }
                },
                enabled = estado.id != null && !cargandoFoto && !estado.guardando
            ) { Text("Cargar imagen") }
        }
        if (estado.id == null) {
            Text("Completa y guarda el registro existente para asociar tu foto.")
        }
        OutlinedTextField(
            value = estado.nombre, onValueChange = viewModel::onPerfilNombreChange,
            label = { Text(if (estado.tipoCuenta == "Empresa") "Representante" else "Nombre") }, singleLine = true,
            isError = estado.errorNombre != null,
            supportingText = { estado.errorNombre?.let { Text(it) } },
            enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()
        )
        if (estado.tipoCuenta == "Empresa") {
            OutlinedTextField(
                value = estado.empresa, onValueChange = viewModel::onPerfilEmpresaChange,
                label = { Text("Nombre de empresa") }, singleLine = true,
                isError = estado.errorEmpresa != null,
                supportingText = { estado.errorEmpresa?.let { Text(it) } },
                enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            value = estado.correo, onValueChange = viewModel::onPerfilCorreoChange,
            label = { Text("Correo electrónico") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = estado.errorCorreo != null,
            supportingText = { estado.errorCorreo?.let { Text(it) } },
            enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.clave, onValueChange = viewModel::onPerfilClaveChange,
            label = { Text("Contraseña") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = estado.errorClave != null,
            supportingText = { estado.errorClave?.let { Text(it) } },
            enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()
        )
        Text("La contraseña se usa solo durante esta sesión de prueba.",
            style = MaterialTheme.typography.bodySmall)
        Button(onClick = viewModel::guardarPerfil, enabled = !estado.guardando && !cargandoFoto,
            modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.guardando) "Guardando…" else "Guardar cambios")
        }
        AnimatedVisibility(estado.mensaje != null) { Text(estado.mensaje.orEmpty()) }
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}

