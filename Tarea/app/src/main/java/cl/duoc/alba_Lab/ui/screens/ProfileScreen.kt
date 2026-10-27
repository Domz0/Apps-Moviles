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
import cl.duoc.alba_Lab.ui.components.ImagenInteligente
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel

@Composable
fun ProfileScreen(mainViewModel: MainViewModel, viewModel: UsuarioViewModel) {
    val estado by viewModel.perfil.collectAsState()
    val solicitudes by viewModel.solicitudes.collectAsState()
    LaunchedEffect(solicitudes) { viewModel.cargarPerfil() }

    val context = LocalContext.current.applicationContext
    val fotos = remember(context) { FotoPerfilStorage(context) }
    val foto by produceState<Bitmap?>(null, estado.id, estado.revisionFoto) {
        value = null
        estado.id?.let { value = fotos.cargar(it) }
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
                ImagenInteligente(foto)
                Text(estado.nombre.ifBlank { "Sin nombre registrado" },
                    style = MaterialTheme.typography.titleLarge)
                Text(estado.correo.ifBlank { "Sin correo registrado" })
            }
        }
        Button(
            onClick = { mainViewModel.navigateTo(Screen.EditarPerfil) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Editar perfil") }
        OutlinedButton(
            onClick = { mainViewModel.navigateTo(Screen.Solicitudes) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver solicitudes") }
        OutlinedButton(
            onClick = {
                // Limpia la solicitud anterior para crear una nueva, conservando el perfil.
                viewModel.nuevaSolicitud()
                mainViewModel.navigateTo(Screen.Registro)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear solicitud") }
    }
}
