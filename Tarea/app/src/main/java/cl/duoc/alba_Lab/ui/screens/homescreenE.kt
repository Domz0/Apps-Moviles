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

// Vista expandida: logo más grande y acciones en una fila inferior.
@Composable
fun HomeScreenE(modifier: Modifier = Modifier, onSettings: () -> Unit = {}) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Bienvenido!", color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium)
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo de Alba",
            modifier = Modifier.fillMaxWidth().height(280.dp),
            contentScale = ContentScale.Fit
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Aplicación funcionando correctamente", color = MaterialTheme.colorScheme.secondary)
            Button(onClick = onSettings) { Text("Ir a Configuración") }
        }
    }
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
fun HomeScreenEPreview() {
    AlbaTheme { HomeScreenE() }
}