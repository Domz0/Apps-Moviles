package cl.duoc.alba_Lab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import cl.duoc.alba_Lab.model.UsuarioUiState

@Composable
fun DatosPersonales(
    datos: UsuarioUiState,
    pedirClave: Boolean,
    onNombre: (String) -> Unit,
    onCorreo: (String) -> Unit,
    onTipo: (String) -> Unit,
    onEmpresa: (String) -> Unit,
    onClave: (String) -> Unit = {}
) {
    OutlinedTextField(datos.nombre, onNombre, label = { Text(if (datos.tipoColaborador == "Empresa") "Representante" else "Nombre") }, singleLine = true,
        enabled = !datos.procesando, modifier = Modifier.fillMaxWidth(),
        isError = datos.errores.nombre != null, supportingText = { datos.errores.nombre?.let { Text(it) } })
    OutlinedTextField(datos.correo, onCorreo, label = { Text("Correo electrónico") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = !datos.procesando, modifier = Modifier.fillMaxWidth(),
        isError = datos.errores.correo != null, supportingText = { datos.errores.correo?.let { Text(it) } })
    if (pedirClave) {
        OutlinedTextField(datos.clave, onClave, label = { Text("Contraseña") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !datos.procesando, modifier = Modifier.fillMaxWidth(),
            isError = datos.errores.clave != null, supportingText = { datos.errores.clave?.let { Text(it) } })
    }
    Text("Tipo de cuenta o colaborador", style = MaterialTheme.typography.titleSmall)
    Row(verticalAlignment = Alignment.CenterVertically) {
        listOf("Persona", "Empresa").forEach { tipo ->
            RadioButton(datos.tipoColaborador == tipo, onClick = { onTipo(tipo) }, enabled = !datos.procesando)
            Text(tipo)
        }
    }
    datos.errores.tipoColaborador?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    if (datos.tipoColaborador == "Empresa") {
        OutlinedTextField(datos.empresa, onEmpresa, label = { Text("Nombre de la empresa") }, singleLine = true,
            enabled = !datos.procesando, modifier = Modifier.fillMaxWidth(),
            isError = datos.errores.empresa != null, supportingText = { datos.errores.empresa?.let { Text(it) } })
    }
}
