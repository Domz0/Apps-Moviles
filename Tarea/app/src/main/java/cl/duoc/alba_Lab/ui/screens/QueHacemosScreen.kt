package cl.duoc.alba_Lab.ui.screens


import androidx.compose.runtime.Composable
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

@Composable
fun QueHacemosScreen(navController: NavController) {
    val iniciativas = listOf(
        "Taller de emprendimiento" to "Fortalece habilidades sociales, emocionales y laborales. Incluye el diseño y la autonomía y el aprendizaje colaborativo.",
        "Intermediación laboral" to "Apoya la inclusión laboral de personas con discapacidad múltiple, facilitando oportunidades para desarrollar sus habilidades en el trabajo.",
        "Asesorías y capacitaciones" to "Acerca la inclusión a empresas y organizaciones mediante asesorías, formación y experiencias de trabajo colaborativo con participantes de Alba Lab.")

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)){
        Text(text = "Nuestros programas", style= MaterialTheme.typography.headlineMedium)
        Text(text = "Conoce las iniciativas de Alba Lab para impulsar la formación, la participación y las oportunidades laborales.")

        iniciativas.forEach { (titulo, descripcion) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (titulo == "Taller de emprendimiento"){
                        Image(painter = painterResource(R.drawable.taller_emprendimiento),
                            contentDescription = "Taller de emprendimiento de Alba Lab",
                            modifier = Modifier.fillMaxWidth().height(180.dp),contentScale = ContentScale.Fit)
                    }
                    Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                    Text(text = descripcion)
                }
            }
        }

        Text(text = "Información adaptada de albalab.corpaliv.cl para este proyecto académico.", style = MaterialTheme.typography.bodySmall)

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}





