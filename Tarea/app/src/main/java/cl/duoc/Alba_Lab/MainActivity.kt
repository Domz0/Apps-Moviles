package cl.duoc.Alba_Lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.Alba_Lab.ui.theme.HomeScreen
import cl.duoc.Alba_Lab.ui.theme.AlbaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlbaTheme {
                HomeScreen()
            }
        }
    }
}