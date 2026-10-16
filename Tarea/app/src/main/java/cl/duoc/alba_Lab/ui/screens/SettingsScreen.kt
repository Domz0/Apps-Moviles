package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.viewmodel.MainViewModel

// Configuración: pantalla del ejemplo con accesos a Inicio y Perfil.
@Composable
fun SettingsScreen(viewModel: MainViewModel) {

    // Contenido centralizado de la pantalla de configuración.
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de Configuración (Settings)")
        Spacer(Modifier.height(24.dp))
        // Emitir el evento de navegación al ViewModel.
        Button(onClick = { viewModel.navigateTo(Screen.Home) }) {
            Text("Volver al Inicio")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.navigateTo(Screen.Profile) }) {
            Text("Ir a Perfil")
        }
    }
}
