package cl.duoc.alba_Lab.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.duoc.alba_Lab.ui.utils.obtenerWindowSizeClass

@Composable
fun ScreenControl(modifier: Modifier = Modifier, onSettings: () -> Unit) {
    val windowSizeClass = obtenerWindowSizeClass()
    // Mostrar un diseño diferente para cada tamaño de pantalla.
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenC(modifier, onSettings)
        WindowWidthSizeClass.Medium -> HomeScreenM(modifier, onSettings)
        else -> HomeScreenE(modifier, onSettings)
    }
}