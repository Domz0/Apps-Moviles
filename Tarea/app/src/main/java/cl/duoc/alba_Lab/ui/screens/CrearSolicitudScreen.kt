package cl.duoc.alba_Lab.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.components.DatosPersonales
import cl.duoc.alba_Lab.ui.components.AceptarTerminos
import cl.duoc.alba_Lab.ui.components.ImagenesSolicitud
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel

@Composable
fun CrearSolicitudScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        viewModel.adjuntarImagenes(it)
    }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(if (estado.esInvitado) "Colaborar como invitado" else "Crear solicitud",
            style = MaterialTheme.typography.headlineMedium)
        if (estado.esInvitado) {
            DatosPersonales(estado, false, viewModel::onNombreChange, viewModel::onCorreoChange,
                viewModel::onTipoColaboradorChange, viewModel::onEmpresaChange)
        }
        Text("¿Cómo deseas colaborar?", style = MaterialTheme.typography.titleMedium)
        UsuarioViewModel.modalidades.forEach { opcion ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(estado.modalidad == opcion, { viewModel.onModalidadChange(opcion) }, enabled = !estado.procesando)
                Text(opcion)
            }
        }
        estado.errores.modalidad?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(estado.mensaje, viewModel::onMensajeChange,
            label = { Text("Mensaje (opcional)") }, minLines = 3,
            enabled = !estado.procesando, modifier = Modifier.fillMaxWidth())
        OutlinedButton(onClick = {
            try { selector.launch("image/*") }
            catch (e: android.content.ActivityNotFoundException) {
                viewModel.informarSolicitud("No hay una aplicación disponible para seleccionar imágenes.")
            }
        }, enabled = !estado.procesando) { Text("Adjuntar imágenes (opcional)") }
        ImagenesSolicitud(estado.imagenes, if (estado.procesando) null else viewModel::quitarImagen)
        if (estado.procesando) CircularProgressIndicator()
        if (estado.esInvitado) {
            AceptarTerminos(estado.aceptaTerminos, estado.errores.aceptaTerminos,
                !estado.procesando, viewModel::onTerminosChange)
        }
        estado.aviso?.let { Text(it) }
        Button(onClick = {
            if (viewModel.validarFormulario()) navController.navigate(Screen.Resumen.route) { launchSingleTop = true }
        }, enabled = !estado.procesando, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
