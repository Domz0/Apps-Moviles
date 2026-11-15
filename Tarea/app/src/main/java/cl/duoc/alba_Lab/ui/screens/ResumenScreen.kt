package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.components.ImagenesSolicitud
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val cuenta by viewModel.sesion.collectAsState()
    val scope = rememberCoroutineScope()

    var guardando by remember { mutableStateOf(false) }
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }

    if (guardando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()}
        return
    }

    Column(modifier = Modifier .fillMaxSize() .wrapContentWidth().widthIn(max = 600.dp) .fillMaxWidth()
            .verticalScroll(rememberScrollState()) .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {Text(text = "Resumen de colaboración", style = MaterialTheme.typography.headlineMedium)
        Text("Revisa tu solicitud antes de guardarla.")

        if (estado.esInvitado) { Text("Invitado: ${estado.nombre}")
            Text("Correo: ${estado.correo}")
            Text("Tipo: ${estado.tipoColaborador}")
            if (estado.tipoColaborador == "Empresa") {Text("Empresa: ${estado.empresa}")}
        } else {Text("Cuenta: ${cuenta?.nombre.orEmpty()}")}

        Text("Forma de colaborar: ${estado.modalidad}")
        Text("Mensaje: ${estado.mensaje.ifBlank {"Sin mensaje"}}")
        ImagenesSolicitud(estado.imagenes)

        estado.aviso?.let {Text(text = it, color = MaterialTheme.colorScheme.error)}

        Button(onClick = { if (guardando || estado.procesando || mostrarConfirmacion) {return@Button}
                val esInvitado = estado.esInvitado
                guardando = true

                scope.launch {
                    try {
                        if (viewModel.guardarSolicitud()) {
                            if (esInvitado) {mostrarConfirmacion = true
                            } else {
                                navController.navigate(Screen.Solicitudes.route) {popUpTo(Screen.CrearSolicitud.route){
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    } finally {guardando = false}
                }
            },
            enabled = !estado.procesando && !mostrarConfirmacion,
            modifier = Modifier.fillMaxWidth()
        ) {Text("Guardar solicitud")}

        TextButton(onClick = { navController.popBackStack() },enabled = !estado.procesando && !mostrarConfirmacion
        ) {Text("Volver y editar")}
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Solicitud enviada correctamente") },
            confirmButton = {
                TextButton(onClick = {
                        if (!navController.popBackStack(Screen.Home.route, false)){
                            navController.navigate(Screen.Home.route) {popUpTo(Screen.Invitado.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                ) {Text("Volver al inicio")}
            }
        )
    }
}