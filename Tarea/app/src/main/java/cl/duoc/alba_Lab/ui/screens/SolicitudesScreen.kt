package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.alba_Lab.model.Solicitud
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.components.ImagenesSolicitud
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SolicitudesScreen(navController: NavController, viewModel: UsuarioViewModel, debug: Boolean = false) {
    val todas by viewModel.solicitudes.collectAsState()
    val cuenta by viewModel.sesion.collectAsState()
    val solicitudes = if (debug) todas else viewModel.solicitudesDeCuenta(todas, cuenta)
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var solicitudAEliminar by remember { mutableStateOf<Solicitud?>(null) }

    // separa entre toooodas las solicitudes y la de cada usuario
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text(if (debug) "Historial de solicitudes (debug)" else "Mis solicitudes", style = MaterialTheme.typography.headlineMedium) }
        if (debug) {
            item { Text("Historial de prueba") }
        } else {
            item {
                Button(onClick = {
                    viewModel.nuevaSolicitud()
                    navController.navigate(Screen.CrearSolicitud.route) { launchSingleTop = true }
                }, modifier = Modifier.fillMaxWidth()) { Text("Nueva solicitud") }
            }
        }
        if (solicitudes.isEmpty()) item { Text("Todavía no hay solicitudes guardadas.") }
        items(solicitudes, key = { it.id }) { solicitud ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (debug) {
                        Text(if (solicitud.cuentaId == null) "Invitado · ${solicitud.tipoColaborador}" else "Cuenta ${solicitud.tipoColaborador}",
                            style = MaterialTheme.typography.labelMedium)
                        Text(solicitud.nombre)
                        Text(solicitud.correo, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("Tipo: ${solicitud.modalidad}", style = MaterialTheme.typography.titleMedium)
                    Text("Fecha: ${solicitud.fechaCreacion?.let { formatoFecha.format(Date(it)) } ?: "No registrada"}", style = MaterialTheme.typography.bodySmall)
                    Text("Mensaje: ${solicitud.mensaje.ifBlank { "Sin mensaje" }}")
                    ImagenesSolicitud(UsuarioViewModel.imagenesDe(solicitud))
                    if (!debug) {
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                if (viewModel.editarSolicitud(solicitud.id)) navController.navigate(Screen.CrearSolicitud.route) { launchSingleTop = true }
                            }, modifier = Modifier.fillMaxWidth()) { Text("Editar") }
                            OutlinedButton(onClick = { solicitudAEliminar = solicitud }, modifier = Modifier.fillMaxWidth()) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
    solicitudAEliminar?.let { solicitud ->
        AlertDialog(onDismissRequest = { solicitudAEliminar = null }, title = { Text("Eliminar solicitud") },
            text = { Text("¿Quieres eliminar esta solicitud de ${solicitud.modalidad}?") },
            confirmButton = {
                TextButton(onClick = { viewModel.eliminarSolicitud(solicitud.id); solicitudAEliminar = null }) { Text("Eliminar") }
            }, dismissButton = { TextButton(onClick = { solicitudAEliminar = null }) { Text("Cancelar") } })
    }
}
