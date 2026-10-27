package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.components.DatosPersonales
import cl.duoc.alba_Lab.ui.components.AceptarTerminos
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

@Composable
fun RegistroScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.registro.collectAsState()
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        DatosPersonales(estado, true,
            onNombre = { valor -> viewModel.actualizarRegistro { it.copy(nombre = valor) } },
            onCorreo = { valor -> viewModel.actualizarRegistro { it.copy(correo = valor) } },
            onTipo = { valor -> viewModel.actualizarRegistro { it.copy(tipoColaborador = valor, empresa = if (valor == "Persona") "" else it.empresa) } },
            onEmpresa = { valor -> viewModel.actualizarRegistro { it.copy(empresa = valor) } },
            onClave = { valor -> viewModel.actualizarRegistro { it.copy(clave = valor) } })
        OutlinedTextField(
            value = estado.direccion,
            onValueChange = { valor -> viewModel.actualizarRegistro { it.copy(direccion = valor) } },
            label = { Text("Dirección") }, enabled = !estado.procesando,
            modifier = Modifier.fillMaxWidth(),
            isError = estado.errores.direccion != null,
            supportingText = { estado.errores.direccion?.let { Text(it) } }
        )
        AceptarTerminos(estado.aceptaTerminos, estado.errores.aceptaTerminos, !estado.procesando) { valor ->
            viewModel.actualizarRegistro { it.copy(aceptaTerminos = valor) }
        }
        estado.aviso?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            scope.launch(Dispatchers.Main.immediate) {
                if (viewModel.registrarCuenta()) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                }
            }
        }, enabled = !estado.procesando, modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.procesando) "Creando cuenta…" else "Registrar")
        }
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
