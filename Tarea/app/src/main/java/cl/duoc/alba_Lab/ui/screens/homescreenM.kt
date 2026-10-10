package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.R
import cl.duoc.alba_Lab.ui.theme.AlbaTheme

// Vista mediana: logo y contenido en una fila.
@Composable
fun HomeScreenM(modifier: Modifier = Modifier, onSettings: () -> Unit = {}) {
    Row(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo de Alba",
            modifier = Modifier.weight(1f).height(200.dp),
            contentScale = ContentScale.Fit
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("¡Bienvenido!", color = MaterialTheme.colorScheme.primary)
            Button(onClick = onSettings) { Text("Ir a Configuración") }
            Text("Aplicación funcionando correctamente", color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 900)
@Composable
fun HomeScreenMPreview() {
    AlbaTheme { HomeScreenM() }
}