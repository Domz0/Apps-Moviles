package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.R
import cl.duoc.alba_Lab.ui.theme.LocalAlbaDarkTheme
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
                    //Text("Menú", modifier = Modifier.padding(16.dp))
                    NavigationDrawerItem(
                        icon = {
                            Image(
                                painter = painterResource(
                                    if (LocalAlbaDarkTheme.current) R.drawable.mark_dark
                                    else R.drawable.mark_light
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        label = {
                            Image(
                                painter = painterResource(
                                    if (LocalAlbaDarkTheme.current) R.drawable.wordmark_dark
                                    else R.drawable.wordmark_light
                                ),
                                contentDescription = "Alba Conecta, ir a Inicio",
                                modifier = Modifier.width(190.dp).height(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                viewModel.navigateTo(Screen.Home)
                            }
                        }
                    )
                    //NavigationDrawerItem(label = {Text("Configuración") },
                    //    selected = false,
                    //    onClick = {scope.launch {drawerState.close()
                    //        viewModel.navigateTo(Screen.Settings)}
                    //    }
                    //)
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
                    title = {
                        Image(
                            painter = painterResource(
                                if (LocalAlbaDarkTheme.current) R.drawable.wordmark_dark
                                else R.drawable.wordmark_light
                            ),
                            contentDescription = "Alba Conecta",
                            modifier = Modifier.width(150.dp).height(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    },
                    navigationIcon = {
                        // Hamburguesa
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }, // deje el boton para configurar, asi despues dejamos todo lo de accesibilidad aqui
                    actions = {
                        IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Abrir configuración")
                        }
                    }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

