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
import cl.duoc.alba_Lab.viewmodel.MainViewModel

@Composable
fun LoginScreen(mainViewModel: MainViewModel, viewModel: LoginViewModel) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().imePadding()
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Text("Ingresa tu correo electrónico y contraseña.")

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = estado.errorCorreo != null,
            supportingText = { estado.errorCorreo?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = estado.errorClave != null,
            supportingText = { estado.errorClave?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = viewModel::validarFormulario,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        if (estado.datosValidos) {
            Text(
                "pasa, pero no conecta aun",
                color = MaterialTheme.colorScheme.primary
            )
        }
        TextButton(onClick = { mainViewModel.navigateBack() }) { Text("Volver") }
    }
}
