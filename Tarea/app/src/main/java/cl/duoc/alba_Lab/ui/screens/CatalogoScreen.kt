package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.R
import cl.duoc.alba_Lab.viewmodel.MainViewModel

@Composable
fun CatalogoScreen(viewModel: MainViewModel) {
    val elementos = listOf(
        Triple("Difusor de aroma", R.drawable.difusor_aroma, "Disponible en el catalogo de Alba Lab en diferentes formatos"),
        Triple("Difusor colagante", R.drawable.difusor_colgante, "Una de las alternativas de la línea de bienestar de Alba Lab"))
    Muestra("Catálogo") {
        Text(text = "Productos con propósito", style= MaterialTheme.typography.titleLarge)
        Text(text= "Conoce productos elaborados por jóvenes y adultos de Alba Lab, en un espacio que impulsa sus habilidades y oportunidades.")
        Text(text="¿Te interesa algún producto? Completa el formulario en «Quiero colaborar» y selecciona «Consulta por productos y servicios».")
        Text(text = "Catálogo de referencia para fines académicos. Las solicitudes de esta app son demostrativas y no se envían a CORPALIV.", style = MaterialTheme.typography.bodySmall)
        // serian las noticias o colabs
        TextButton(onClick = { viewModel.navigateBack() }) { Text("Volver") }
        elementos.forEach { (nombre, imagen, descripcion) ->
            Card(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(imagen),
                        contentDescription = nombre,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text= descripcion)
                }
            }
        }
    }
}
@Composable
private fun Muestra(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    MaterialTheme { Surface { Marco(titulo, contenido) } }
}
@Composable
private fun Marco(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        contenido()
    }
}
