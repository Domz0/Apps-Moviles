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
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun SolicitudesScreen(navController: NavController,viewModel: UsuarioViewModel){
    val solicitudes by viewModel.solicitudes.collectAsState()

    var solicitudAEliminar by remember {mutableStateOf<Solicitud?>(null)}

    LazyColumn(modifier = Modifier.fillMaxSize(),contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {item {Text(text = "Mis solicitudes",style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Button(onClick = {viewModel.nuevaSolicitud()
                    navController.navigate(Screen.Registro.route) {launchSingleTop = true}
                }, modifier = Modifier.fillMaxWidth()) {Text("Nueva solicitud")}
        }

        if (solicitudes.isEmpty()) {item {Text("Todavía no tienes solicitudes guardadas.")}}

        items(items = solicitudes,key = { it.id }
        ) { solicitud ->
            var mostrarDetalles by rememberSaveable{mutableStateOf(false) }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = solicitud.modalidad,style = MaterialTheme.typography.titleMedium)
                    Text("Nombre: ${solicitud.nombre}")
                    TextButton(onClick = {mostrarDetalles = !mostrarDetalles}
                    ) {Text(if (mostrarDetalles) {"Ocultar detalles"
                        } else {"Ver detalles"}
                        )
                    }

                    AnimatedVisibility(visible = mostrarDetalles) {Column(verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Correo: ${solicitud.correo}")
                            Text("Tipo: ${solicitud.tipoColaborador}")

                            if (solicitud.tipoColaborador == "Empresa") {Text("Empresa: ${solicitud.empresa}")}
                            if (solicitud.direccion.isNotBlank()) {Text("Dirección: ${solicitud.direccion}")}
                            Text("Mensaje: ${solicitud.mensaje}")
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = {
                                if (viewModel.editarSolicitud(solicitud.id)) {navController.navigate(Screen.Registro.route) {launchSingleTop = true}}
                            }, modifier = Modifier.weight(1f)){Text("Editar")}

                        OutlinedButton(
                            onClick = {solicitudAEliminar = solicitud},
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {Text("Eliminar")}
                    }
                }
            }
        }
        //este aviso aparece una sola vez
        item {Text(text = "Guardado temporal: las solicitudes no se conservan al cerrar el proceso de la app.", style = MaterialTheme.typography.bodySmall)
        }
    }

    //el diálogo queda fuera de LazyC
    solicitudAEliminar?.let { solicitud -> AlertDialog(onDismissRequest = {solicitudAEliminar = null },
            title = {Text("Eliminar solicitud")},
            text = {Text("¿Quieres eliminar la solicitud de " + "${solicitud.nombre} para ${solicitud.modalidad}?")
            },
            confirmButton = {
                TextButton(onClick = {viewModel.eliminarSolicitud(solicitud.id)
                    solicitudAEliminar = null}
                ) {Text(text = "Eliminar",color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {solicitudAEliminar = null}
                ) {Text("Cancelar")}
            }
        )
    }
}