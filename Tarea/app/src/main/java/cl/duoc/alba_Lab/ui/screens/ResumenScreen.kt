package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import cl.duoc.alba_Lab.navigation.Screen

// Lee los datos del mismo ViewModel utilizado por el formulario.
@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel
) {
    val estado by viewModel.estado.collectAsState()
    Column(modifier = Modifier
            .fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Resumen de colaboración", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Revisa los datos ingresados antes de continuar.", style = MaterialTheme.typography.bodyLarge)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Datos del usuario", style = MaterialTheme.typography.titleMedium)
                Text("Nombre: ${estado.nombre}")
                Text("Correo: ${estado.correo}")
                Text("Dirección: ${estado.direccion}")

                // Ocultamos la contraseña en la pantalla
                Text(text = "Contraseña: ${"*".repeat(estado.clave.length)}")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Datos de colaboración", style = MaterialTheme.typography.titleMedium)
                Text(text = "Tipo de colaborador: ${estado.tipoColaborador}")

                // Solo muestra empresa cuando corresponde
                if (estado.tipoColaborador == "Empresa") { Text("Empresa: ${estado.empresa}") }

                Text("Modalidad: ${estado.modalidad}")
                Text("Mensaje: ${estado.mensaje}")
            }
        }
        Text(text = "Términos: ${if (estado.aceptaTerminos) {"Aceptados"
        } else{ "No aceptados" }}")

        Button(onClick = {
            if (viewModel.guardarSolicitud()) {navController.navigate(Screen.Solicitudes.route) {
                        popUpTo(Screen.Registro.route) {inclusive = true}
                        launchSingleTop = true}}}, modifier = Modifier.fillMaxWidth()){
            Text("Guardar solicitud")
        }
        // Regresa al formulario conservando los datos
        Button(onClick = {navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver y editar") }
    }
}