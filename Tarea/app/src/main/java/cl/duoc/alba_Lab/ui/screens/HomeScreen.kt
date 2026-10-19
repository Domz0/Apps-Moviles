package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.R
import cl.duoc.alba_Lab.ui.theme.AlbaTheme
import cl.duoc.alba_Lab.ui.theme.LocalAlbaDarkTheme
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    HomeScreenContent(onSettings = { viewModel.navigateTo(Screen.Settings) })
}
// define como se ve segun tamaño
@Composable
private fun HomeScreenContent(modifier: Modifier = Modifier, onSettings: () -> Unit = {}) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val compacta = maxWidth < 600.dp
        val extendida = maxWidth >= 840.dp
        val margen = if (extendida) 40.dp else 24.dp
        val alturaLogo = if (compacta) 150.dp else if (extendida) 280.dp else 200.dp
        val scroll = rememberScrollState()

        if (compacta) {
            Column(
                Modifier.fillMaxSize().verticalScroll(scroll).padding(margen),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeLogo(Modifier.fillMaxWidth().height(alturaLogo))
                HomeWelcome(onSettings, centrado = true)
            }
        } else {
            Row(
                Modifier.fillMaxSize().verticalScroll(scroll).padding(margen),
                horizontalArrangement = Arrangement.spacedBy(margen),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeLogo(Modifier.weight(1f).height(alturaLogo))
                HomeWelcome(onSettings, modifier = Modifier.weight(1f))
            }
        }
    }
}
@Composable
private fun HomeLogo(modifier: Modifier = Modifier) {
    val oscuro = LocalAlbaDarkTheme.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(if (oscuro) R.drawable.mark_dark else R.drawable.mark_light),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(if (oscuro) R.drawable.wordmark_dark else R.drawable.wordmark_light),
            contentDescription = "Alba Conecta",
            modifier = Modifier.widthIn(max = 260.dp).fillMaxWidth().height(38.dp),
            contentScale = ContentScale.Fit
        )
    }
}
@Composable
private fun HomeWelcome(
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    centrado: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = if (centrado) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text("¡Bienvenido!", color = MaterialTheme.colorScheme.primary)
        //Button(onClick = onSettings) { Text("Ir a Configuración") }
        //Text("Aplicación funcionando correctamente", color = MaterialTheme.colorScheme.secondary)
        Marco("Alba Conecta") {
            Tarjeta("Resumen", "aqui dejamos algo como el propsito apoyo e informacion.")
            Text("bottom text con algo extra(?)")
        }
    }
}
@Composable
private fun Marco(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        contenido()
    }
}
@Composable
private fun Tarjeta(titulo: String, descripcion: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(descripcion)
        }
    }
}

@Preview(name = "Inicio compacto", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Inicio medio", showBackground = true, widthDp = 700, heightDp = 900)
@Preview(name = "Inicio extendido", showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
private fun HomeScreenContentPreview() {
    AlbaTheme { Surface { HomeScreenContent() } }
}
