package cl.duoc.alba_Lab.ui.screens


import androidx.compose.runtime.Composable
import android.content.res.Configuration
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import cl.duoc.alba_Lab.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.alba_Lab.viewmodel.ProgramasEstado
import cl.duoc.alba_Lab.viewmodel.ProgramasViewModel
@Composable
fun QueHacemosScreen(navController: NavController) {
    val horizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val programasViewModel: ProgramasViewModel = viewModel()
    val estado by programasViewModel.estado.collectAsState()
    val programas = (estado as? ProgramasEstado.Disponible)?.programas.orEmpty()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)){
        Text(text = "Nuestros programas", style= MaterialTheme.typography.headlineMedium)
        Text(text = "Conoce las iniciativas de Alba Lab para impulsar la formación, la participación y las oportunidades laborales.")

        when (estado) {ProgramasEstado.Cargando -> Text("Cargando programas…")
            ProgramasEstado.Error -> {Text("No se pudieron cargar los programas.")
                TextButton(onClick = programasViewModel::cargar) {Text("Reintentar") }
            }
            is ProgramasEstado.Disponible -> {
                if (programas.isEmpty()) Text("No hay programas disponibles.") }
        }
        programas.forEach { programa ->
            val titulo = programa.nombre
            val descripcion = programa.descripcion
            val esTaller = programa.id == "taller-emprendimiento"
            Card(modifier = Modifier.fillMaxWidth()) {
                if (horizontal && esTaller) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.taller_emprendimiento),
                            contentDescription = "Taller de emprendimiento de Alba Lab",
                            modifier = Modifier.weight(1f).height(180.dp),
                            contentScale = ContentScale.Fit
                        )
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                            Text(text = descripcion)
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (titulo == "Taller de emprendimiento") {
                            Image(painter = painterResource(R.drawable.taller_emprendimiento),
                                contentDescription = "Taller de emprendimiento de Alba Lab",
                                modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Fit)
                        }
                        Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                        Text(text = descripcion)
                    }
                }
            }
        }

        Text(text = "Información adaptada de albalab.corpaliv.cl para este proyecto académico.", style = MaterialTheme.typography.bodySmall)

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}





