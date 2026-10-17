package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun AlbaScaffold(viewModel: MainViewModel, content: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text("Menú", modifier = Modifier.padding(16.dp))
                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                viewModel.navigateTo(Screen.Home)
                            }
                        }
                    )
                    NavigationDrawerItem(label = {Text("Configuración") },
                        selected = false,
                        onClick = {scope.launch {drawerState.close()
                                viewModel.navigateTo(Screen.Settings)}
                        }
                    )
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
                        label = { Text("Iniciar sesion") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                viewModel.navigateTo(Screen.Profile)// por modificar
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Catálogo") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                viewModel.navigateTo(Screen.Catalogo)
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Ir a Perfil (tras login)") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                viewModel.navigateTo(Screen.Profile)// por modificar
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Editar Perfil (tras login)") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                viewModel.navigateTo(Screen.Profile)// por modificar
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Mis solicitudes")},
                        selected = false,
                        onClick = {scope.launch {drawerState.close()
                                viewModel.navigateTo(Screen.Solicitudes)
                            }
                        }
                    )
                }
            }
        }
    ) {
        // Barra superior y menú compartido
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Alba") },
                    navigationIcon = {
                        // Hamburguesa
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

