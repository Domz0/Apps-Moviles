package cl.duoc.alba_Lab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.data.CatalogoProductos
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.ui.utils.obtenerWindowSizeClass
import cl.duoc.alba_Lab.viewmodel.CarritoViewModel
import androidx.navigation.NavController
import cl.duoc.alba_Lab.viewmodel.CatalogoWebViewModel
import cl.duoc.alba_Lab.model.Producto
import cl.duoc.alba_Lab.viewmodel.CatalogoWebEstado
@Composable
fun CatalogoScreen(navController: NavController, carritoViewModel: CarritoViewModel, catalogoWebViewModel: CatalogoWebViewModel) {
    //obs estado de la consulta a la api
    val estadoWeb by catalogoWebViewModel.estado.collectAsState()
    //observa los productos agregados al carrito
    val carrito by carritoViewModel.carrito.collectAsState()
    val procesando by carritoViewModel.procesando.collectAsState()
    val error by carritoViewModel.error.collectAsState()

    //obtiene el tamaño de la ventana usando la función de la guía
    val windowSizeClass = obtenerWindowSizeClass()

    //cambia las columnas según el ancho disponible
    val columnas = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }

    // Carga los productos al entrar al catálogo.
    LaunchedEffect(catalogoWebViewModel) {
        catalogoWebViewModel.cargar()
    }
    //se usa los datos de la api y se conserva el ID y la foto del producto local
    val elementos = when (val estado = estadoWeb) {is CatalogoWebEstado.Disponible -> estado.productos.mapNotNull {remoto ->
            val idLocal = when (remoto.id) {
                1 -> "difusor_aroma_100"
                2 -> "difusor_colgante"
                else -> return@mapNotNull null
            }
            val local = CatalogoProductos.elementos.find {it.id == idLocal}
                ?: return@mapNotNull null
            Producto(id = local.id, nombre = remoto.nombre, imagen = local.imagen, descripcion = remoto.descripcion, precio = remoto.precio)
        }
        else -> emptyList()
    }

    //cuadrícula con desplazamiento y columnas adaptables
    LazyVerticalGrid(columns = GridCells.Fixed(columnas), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Catálogo", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Productos con propósito", style = MaterialTheme.typography.titleLarge)

                Text(text = "Conoce productos elaborados por jóvenes y adultos de Alba Lab, en un espacio que impulsa sus habilidades y oportunidades.")

                Text(text = "¿Quieres ayudar? Elige 'Crear solicitud' desde Mi perfil o 'Colaborar como invitado' en el menú.")

                Text(text = "Catálogo de referencia para fines académicos. Las solicitudes de esta app son demostrativas y no se envían a CORPALIV.", style = MaterialTheme.typography.bodySmall)

                TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }

                if (carritoViewModel.conSesion) {
                    Text(text = "Unidades en el carrito: ${carrito.sumOf { it.cantidad }}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Total presupuestado: \$${carritoViewModel.calcularTotal()}", style = MaterialTheme.typography.titleLarge)
                } else {
                    Text("Inicia sesión para guardar productos en tu carrito.")
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
        //muestra el estado de la consulta en una filita de ancho completo
            if (elementos.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    when (estadoWeb) {CatalogoWebEstado.Inicial, CatalogoWebEstado.Cargando -> Text("Cargando catálogo…")
                        CatalogoWebEstado.Error -> Column {Text("No se pudo cargar el catálogo.")
                            TextButton(onClick = catalogoWebViewModel::cargar) {Text("Reintentar")}
                        }
                        is CatalogoWebEstado.Disponible -> Text("No hay productos disponibles.")
                    }
                }
            }

        //cada producto ocupa una celda y se identifica por su id
        items(items = elementos, key = { it.id }) { producto ->
            //conservamos si los detalles del producto están abiertos
            var mostrarDetalles by rememberSaveable { mutableStateOf(false) }

            //busca cuantas unidades de este producto  hay en el carrito
            val cantidad = carrito.find { it.producto.id == producto.id }?.cantidad ?: 0

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Image(painter = painterResource(producto.imagen), contentDescription = producto.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp), contentScale = ContentScale.Fit)
                    Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)

                    TextButton(onClick = { mostrarDetalles = !mostrarDetalles }
                    ) {
                        Text(if (mostrarDetalles) {
                                "Ocultar detalles"
                            } else {
                                "Ver detalles"
                            }
                        )
                    }
                    AnimatedVisibility(visible = mostrarDetalles) { Text(text = producto.descripcion) }

                    Text(text = "Precio de referencia: \$${producto.precio}")

                    //se agrega una unidad al carrito
                    Button(onClick = {
                            if (carritoViewModel.conSesion) carritoViewModel.agregarProducto(producto)
                            else navController.navigate(Screen.Login.route) { launchSingleTop = true }
                        },
                        enabled = !procesando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (carritoViewModel.conSesion) "Agregar al carrito" else "Iniciar sesión para agregar")
                    }

                    //solo se muestra si hay unidades agregadas
                    if (cantidad > 0) { Text(text = "En el carrito: $cantidad")

                        TextButton(onClick = { carritoViewModel.quitarUnidad(producto.id)}, enabled = !procesando
                        ) {
                            Text("Quitar una unidad")
                        }

                        TextButton(onClick = {carritoViewModel.eliminarProducto(producto.id)}, enabled = !procesando
                        ) {
                            Text("Eliminar del carrito")
                        }
                    }
                }
            }
        }
    }
}
