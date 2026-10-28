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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbaScaffold(navController: NavController, usuarioViewModel: UsuarioViewModel, content: @Composable (PaddingValues) -> Unit) {
    val sesion by usuarioViewModel.sesion.collectAsState()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val seccionActual = when (rutaActual) {
        Screen.EditarPerfil.route, Screen.Solicitudes.route, Screen.CrearSolicitud.route -> Screen.Profile.route
        Screen.Resumen.route -> if (sesion == null) Screen.Invitado.route else Screen.Profile.route
        else -> rutaActual
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    val opciones = if (sesion == null) listOf(
                        "Registrar" to Screen.Registro,
                        "Iniciar" to Screen.Login,
                        "Colaborar como invitado" to Screen.Invitado,
                        "Catálogo" to Screen.Catalogo,
                        "Nuestros programas" to Screen.QueHacemos,
                        "Contacto" to Screen.Contacto,
                        "Historial de solicitudes (debug)" to Screen.HistorialDebug
                    // logueado
                    ) else listOf(
                        "Mi perfil" to Screen.Profile,
                        "Catálogo" to Screen.Catalogo,
                        "Carrito" to Screen.Carrito,
                        "Nuestros programas" to Screen.QueHacemos,
                        "Contacto" to Screen.Contacto,
                        "Historial de solicitudes (debug)" to Screen.HistorialDebug
                    )
                    opciones.forEach { (titulo, pantalla) ->
                        NavigationDrawerItem(
                            label = { Text(titulo) },
                            selected = seccionActual == pantalla.route,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    if (pantalla == Screen.Invitado) usuarioViewModel.nuevaSolicitud(invitado = true)
                                    navController.navigate(pantalla.route) { if (pantalla == Screen.Home) popUpTo(Screen.Home.route); launchSingleTop = true }
                                }
                            }
                        )
                    }
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
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } }) {
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
