package cl.duoc.alba_Lab.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.data.FotoPerfilStorage
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.components.FotoPerfil
import androidx.navigation.NavController
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.perfil.collectAsState()
    val sesion by viewModel.sesion.collectAsState()
    val cuenta = sesion ?: return

    val context = LocalContext.current.applicationContext
    val fotos = remember(context) { FotoPerfilStorage(context) }
    val foto by produceState<Bitmap?>(null, cuenta.id, estado.revisionFoto) {
        value = null
        value = fotos.cargar(cuenta.id)
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FotoPerfil(foto)
                if (cuenta.tipo == "Empresa") {
                    Text("Nombre de empresa", style = MaterialTheme.typography.labelMedium)
                    Text(cuenta.empresa, style = MaterialTheme.typography.titleLarge)
                    Text("Representante", style = MaterialTheme.typography.labelMedium)
                    Text(cuenta.nombre)
                } else {
                    Text(cuenta.nombre, style = MaterialTheme.typography.titleLarge)
                }
                Text(cuenta.correo)
                if (cuenta.tipo in listOf("Persona", "Empresa")) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text("Cuenta ${cuenta.tipo}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
        Button(
            onClick = { navController.navigate(Screen.EditarPerfil.route) { launchSingleTop = true } },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Editar perfil") }
        OutlinedButton(
            onClick = { navController.navigate(Screen.Solicitudes.route) { launchSingleTop = true } },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver solicitudes") }
        OutlinedButton(
            onClick = {
                // Limpia la solicitud anterior para crear una nueva, conservando el perfil.
                viewModel.nuevaSolicitud()
                navController.navigate(Screen.CrearSolicitud.route) { launchSingleTop = true }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear solicitud") }
        TextButton(onClick = {
            viewModel.cerrarSesion()
            navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route); launchSingleTop = true }
        }) { Text("Cerrar sesión") }
    }
}
