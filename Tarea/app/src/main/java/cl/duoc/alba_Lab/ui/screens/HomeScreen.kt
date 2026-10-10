package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Menú lateral para acceder al perfil.
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Ir a Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            viewModel.navigateTo(Screen.Profile)
                        }
                    }
                )
            }
        }
    ) {
        // Estructura base de Inicio con barra superior.
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Alba") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Selecciona la vista según el tamaño de la ventana.
            ScreenControl(
                modifier = Modifier.padding(innerPadding),
                onSettings = { viewModel.navigateTo(Screen.Settings) }
            )
        }
    }
}