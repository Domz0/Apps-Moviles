package cl.duoc.alba_Lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.alba_Lab.navigation.AppNavigation
import cl.duoc.alba_Lab.ui.theme.AlbaTheme

// Punto de entrada: aplica el tema e inicia la navegación de la aplicación.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlbaTheme {
                AppNavigation()
            }
        }
    }
}
