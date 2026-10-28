package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import cl.duoc.alba_Lab.viewmodel.ModoTema
import cl.duoc.alba_Lab.viewmodel.TemaViewModel
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(mainViewModel: MainViewModel, temaViewModel: TemaViewModel) {
    val modoTema by temaViewModel.modo.collectAsState()
    var cargando by remember { mutableStateOf(true) }

    // Conserva la carga simulada del ejercicio de animaciones
    LaunchedEffect(Unit) {
        delay(1500)
        cargando = false
    }

    if (cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Configuración", style = MaterialTheme.typography.headlineMedium)
            Column(Modifier.fillMaxWidth().selectableGroup()) {
                Text("Tema", style = MaterialTheme.typography.titleLarge)
                ModoTema.values().forEach { opcion ->
                    Row(
                        Modifier.fillMaxWidth().selectable(
                            selected = modoTema == opcion,
                            onClick = { temaViewModel.cambiarModo(opcion) },
                            role = Role.RadioButton
                        ).padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = modoTema == opcion, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(opcion.titulo)
                    }
                }
            }
            TextButton(onClick = { mainViewModel.navigateBack() }) { Text("Volver") }
        }
    }
}