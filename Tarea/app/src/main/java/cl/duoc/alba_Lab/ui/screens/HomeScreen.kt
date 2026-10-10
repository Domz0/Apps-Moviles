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

    // Drawer: menú lateral con accesos al perfil y al formulario de registro.
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp))
                // Registro: abre el formulario de la guía 11.
                NavigationDrawerItem(
                    label = { Text("Registro de usuario") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            viewModel.navigateTo(Screen.Registro)
                        }
                    }
                )
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
        // Scaffold: organiza la barra superior y el contenido de Inicio.
        Scaffold(
            topBar = {
                // TopAppBar: barra superior que muestra el nombre de la aplicación.
                TopAppBar(
                    title = { Text("Alba") },
                    navigationIcon = {
                        // Hamburguesa: botón de tres líneas que abre el menú lateral.
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
