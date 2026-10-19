package cl.duoc.alba_Lab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.alba_Lab.viewmodel.EstadoViewModel
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import cl.duoc.alba_Lab.viewmodel.ModoTema
import cl.duoc.alba_Lab.viewmodel.TemaViewModel

// Configuración: pantalla del ejemplo con accesos a Inicio y Perfil.
@Composable
fun SettingsScreen(mainViewModel: MainViewModel, temaViewModel: TemaViewModel,
                   estadoViewModel: EstadoViewModel = viewModel()
) {
    val modoTema by temaViewModel.modo.collectAsState()
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp),horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Configuración", style = MaterialTheme.typography.headlineMedium)

            Column(Modifier.fillMaxWidth().selectableGroup()) {
                Text("Tema", style = MaterialTheme.typography.titleLarge)
                ModoTema.values().forEach { opcion ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .selectable(
                                selected = modoTema == opcion,
                                onClick = { temaViewModel.cambiarModo(opcion) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = modoTema == opcion, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(opcion.titulo)
                    }
                }
            }

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
