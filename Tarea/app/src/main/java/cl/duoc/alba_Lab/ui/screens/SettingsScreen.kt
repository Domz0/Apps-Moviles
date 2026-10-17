package cl.duoc.alba_Lab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.alba_Lab.viewmodel.EstadoViewModel
import cl.duoc.alba_Lab.viewmodel.MainViewModel

// Configuración: pantalla del ejemplo con accesos a Inicio y Perfil.
@Composable
fun SettingsScreen(mainViewModel: MainViewModel, estadoViewModel: EstadoViewModel = viewModel()
) {
    val activo by estadoViewModel.activo.collectAsState()
    val mostrarMensaje by estadoViewModel.mostrarMensaje.collectAsState()

    //muestra el indicador mientras se recupera la preferencia
    if (activo == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {CircularProgressIndicator() //aparece durante la carga inicial
        }
    } else {
        val estaActivo = activo == true

        //se cambia el color levemente segun el mensaje // animateColorAsState esto anima el cambio de color del boton
        val colorAnimado by animateColorAsState(targetValue = if (estaActivo) {MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            },
            animationSpec = tween(durationMillis = 500), label = "Color del botón")

        // el texto dependera del estado actual
        val textoBoton by remember(estaActivo) {derivedStateOf {if (estaActivo) "Desactivar" else "Activar"}
        } //obtiene activar o desactivar segun estado

        Column(modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp),horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Configuración", style = MaterialTheme.typography.headlineMedium)

            Text(text = "Modo de colaboración",style = MaterialTheme.typography.titleLarge)

            Text(text = if (estaActivo) {"El modo está activado"
                } else {"El modo está desactivado"}
            )

            Button(onClick = {estadoViewModel.alternarEstado()},
                colors = ButtonDefaults.buttonColors(containerColor = colorAnimado),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(textoBoton)
            }

            AnimatedVisibility(visible = mostrarMensaje) {
                Text(text = "Preferencia guardada",color = MaterialTheme.colorScheme.primary)
            }

            TextButton(onClick = {mainViewModel.navigateBack()}
            ) {
                Text("Volver")
            }
        }
    }
}