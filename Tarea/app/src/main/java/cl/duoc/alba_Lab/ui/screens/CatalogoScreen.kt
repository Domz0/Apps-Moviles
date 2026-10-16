package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
        // titulo e imagen
        "ejemplo" to R.drawable.gato_malandro,
        "ejemplo2" to R.drawable.stock
    )
    Muestra("Catálogo") {
        // serian las noticias o colabs
        TextButton(onClick = { viewModel.navigateBack() }) { Text("Volver") }
        elementos.forEach { (nombre, imagen) ->
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
                    Text("Este texto tambien tiene que cambiar para adecuar a lo que pongamos despues.")
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
        Modifier.fillMaxWidth().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        contenido()
    }
}
