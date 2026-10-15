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

// Vista compacta: los elementos se organizan en una columna.
@Composable
fun HomeScreenC(modifier: Modifier = Modifier, onSettings: () -> Unit = {}) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.alba_logo),
            contentDescription = "Logo de Alba",
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentScale = ContentScale.Fit
        )
        Text("¡Bienvenido!", color = MaterialTheme.colorScheme.primary)
            Button(onClick = onSettings) { Text("Ir a Configuración") }
            Text("Aplicación funcionando correctamente", color = MaterialTheme.colorScheme.secondary)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenCPreview() {
    AlbaTheme { HomeScreenC() }
}