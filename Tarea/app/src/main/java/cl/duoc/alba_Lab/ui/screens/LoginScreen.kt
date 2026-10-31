package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.viewmodel.LoginViewModel
import androidx.navigation.NavController
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import cl.duoc.alba_Lab.navigation.Screen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel, usuarioViewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    var errorSesion by remember { mutableStateOf<String?>(null) }
    var iniciando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().wrapContentWidth().widthIn(max = 600.dp).fillMaxWidth().imePadding()
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Text("Ingresa tu correo electrónico y contraseña.")

        OutlinedTextField(
            value = estado.correo,
            onValueChange = { viewModel.onCorreoChange(it); errorSesion = null },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = estado.errorCorreo != null,
            supportingText = { estado.errorCorreo?.let { Text(it) } },
            singleLine = true,
            enabled = !iniciando,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.clave,
            onValueChange = { viewModel.onClaveChange(it); errorSesion = null },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = estado.errorClave != null,
            supportingText = { estado.errorClave?.let { Text(it) } },
            singleLine = true,
            enabled = !iniciando,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                errorSesion = null
                viewModel.validarFormulario()
                if (viewModel.estado.value.datosValidos) {
                    iniciando = true
                    scope.launch {
                        try {
                            if (usuarioViewModel.iniciarSesionSimulada(estado.correo, estado.clave)) {
                                viewModel.onClaveChange("")
                            } else {
                                errorSesion = "Revisa el correo registrado y la contraseña."
                            }
                        } catch (e: CancellationException) { throw e
                        } catch (e: Exception) { errorSesion = "No se pudo iniciar sesión. Intenta nuevamente."
                        } finally { iniciando = false }
                    }
                }
            },
            enabled = !iniciando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (iniciando) "Iniciando…" else "Iniciar sesión")
        }

        errorSesion?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
