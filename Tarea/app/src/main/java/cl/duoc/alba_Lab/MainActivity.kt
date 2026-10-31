package cl.duoc.alba_Lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.alba_Lab.navigation.AppNavigation
import cl.duoc.alba_Lab.ui.theme.AlbaTheme
import cl.duoc.alba_Lab.viewmodel.ModoTema
import cl.duoc.alba_Lab.viewmodel.TemaViewModel

// inicio :)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val temaViewModel: TemaViewModel = viewModel()
            val modo by temaViewModel.modo.collectAsState()
            val oscuro = when (modo) {
                ModoTema.SISTEMA -> isSystemInDarkTheme()
                ModoTema.CLARO -> false
                ModoTema.OSCURO -> true
            }
            AlbaTheme(darkTheme = oscuro) {
                AppNavigation(temaViewModel)
            }
        }
    }
}
