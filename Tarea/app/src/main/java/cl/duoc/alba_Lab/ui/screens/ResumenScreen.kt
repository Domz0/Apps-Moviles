package cl.duoc.alba_Lab.ui.screens

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
import cl.duoc.alba_Lab.ui.components.ImagenesSolicitud
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val cuenta by viewModel.sesion.collectAsState()
    val scope = rememberCoroutineScope()
    var guardando by remember { mutableStateOf(false) }

    if (guardando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Resumen de colaboración", style = MaterialTheme.typography.headlineMedium)
        Text("Revisa tu solicitud antes de guardarla,")
        if (estado.esInvitado) {
            Text("Invitado: ${estado.nombre}")
            Text("Correo: ${estado.correo}")
            Text("Tipo: ${estado.tipoColaborador}")
            if (estado.tipoColaborador == "Empresa") Text("Empresa: ${estado.empresa}")
        } else {
            Text("Cuenta: ${cuenta?.nombre.orEmpty()}")
        }
        Text("Forma de colaborar: ${estado.modalidad}")
        Text("Mensaje: ${estado.mensaje.ifBlank { "Sin mensaje" }}")
        ImagenesSolicitud(estado.imagenes)
        estado.aviso?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            if (guardando) return@Button
            guardando = true
            // La navegación debe continuar en el hilo principal tras esperar a Room.
            scope.launch(Dispatchers.Main.immediate) {
                try {
                    // La misma carga simulada de 1,5 segundos que en Configuración.
                    delay(1500)
                    if (viewModel.guardarSolicitud()) {
                        navController.navigate(if (estado.esInvitado) Screen.HistorialDebug.route else Screen.Solicitudes.route) {
                            popUpTo(if (estado.esInvitado) Screen.Invitado.route else Screen.CrearSolicitud.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } finally {
                    guardando = false
                }
            }
        }, enabled = !estado.procesando, modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.procesando) "Guardando…" else "Guardar solicitud")
        }
        TextButton(onClick = { navController.popBackStack() }, enabled = !estado.procesando) { Text("Volver y editar") }
    }
}
