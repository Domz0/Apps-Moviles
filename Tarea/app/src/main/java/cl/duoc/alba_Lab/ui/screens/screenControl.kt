package cl.duoc.alba_Lab.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import cl.duoc.alba_Lab.ui.theme.HomeScreenC
import cl.duoc.alba_Lab.ui.theme.HomeScreenE
import cl.duoc.alba_Lab.ui.theme.HomeScreenM
import cl.duoc.alba_Lab.ui.utils.obtenerWindowSizeClass

@Composable
fun ScreenControl() {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenC()
        WindowWidthSizeClass.Medium -> HomeScreenM()
        WindowWidthSizeClass.Expanded -> HomeScreenE()
    }
}