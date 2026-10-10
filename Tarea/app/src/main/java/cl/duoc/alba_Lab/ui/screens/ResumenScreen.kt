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

// Resumen: lee el mismo ViewModel, sin recibir datos como argumentos de la ruta.
@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Resumen del Registro", style = MaterialTheme.typography.headlineMedium)
            Text("Nombre: ${estado.nombre}")
            Text("Correo: ${estado.correo}")
            Text("Dirección: ${estado.direccion}")
            // Clave: se representa con asteriscos, como en el ejemplo de la guía.
            Text("Contraseña: ${"*".repeat(estado.clave.length)}")
            Text("Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")

            // Regresa al formulario conservando los datos compartidos.
            Button(onClick = { navController.popBackStack() }) { Text("Volver al formulario") }
        }
    }
}
