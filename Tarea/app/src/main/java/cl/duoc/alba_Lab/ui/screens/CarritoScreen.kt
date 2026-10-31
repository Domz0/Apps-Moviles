package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.viewmodel.CarritoViewModel
import androidx.navigation.NavController

@Composable
fun CarritoScreen(navController: NavController, viewModel: CarritoViewModel) {
    val carrito by viewModel.carrito.collectAsState()
    val procesando by viewModel.procesando.collectAsState()
    val error by viewModel.error.collectAsState()
    var confirmarVaciado by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Mi carrito", style = MaterialTheme.typography.headlineMedium)
            Text("Productos seleccionados (no se realiza compra).")
        }
        //condicion de si hay contenido
        if (carrito.isEmpty()) item { Text("Carrito vacio :(") }
        items(carrito, key = { it.producto.id }) { item ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(
                        painterResource(item.producto.imagen), item.producto.nombre,
                        Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Fit
                    )
                    Text(item.producto.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("Precio de referencia: $${item.producto.precio}")
                    Text("Cantidad: ${item.cantidad}")
                    Text("Subtotal: $${item.subtotal}")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.quitarUnidad(item.producto.id) }, enabled = !procesando, modifier = Modifier.fillMaxWidth()) {
                            Text("Quitar unidad")
                        }
                        OutlinedButton(onClick = { viewModel.agregarProducto(item.producto) }, enabled = !procesando, modifier = Modifier.fillMaxWidth()) {
                            Text("Agregar unidad")
                        }
                    }
                    TextButton(onClick = { viewModel.eliminarProducto(item.producto.id) }, enabled = !procesando) {
                        Text("Eliminar producto")
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                if (procesando) CircularProgressIndicator()
                Text("Total : $${viewModel.calcularTotal()}", style = MaterialTheme.typography.titleLarge)
                if (carrito.isNotEmpty()) {
                    OutlinedButton(onClick = { confirmarVaciado = true }, enabled = !procesando) { Text("Vaciar carrito") }
                }
                Button(onClick = { navController.navigate(Screen.Catalogo.route) { launchSingleTop = true } }, modifier = Modifier.fillMaxWidth()) {
                    Text("Seguir viendo catálogo")
                }
                TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
            }
        }
    }
    if (confirmarVaciado) {
        AlertDialog(
            onDismissRequest = { confirmarVaciado = false },
            title = { Text("Vaciar carrito") },
            text = { Text("¿Quieres quitar todos los productos de tu carrito?") },
            confirmButton = {
                TextButton(onClick = { viewModel.vaciarCarrito(); confirmarVaciado = false }) { Text("Vaciar") }
            },
            dismissButton = { TextButton(onClick = { confirmarVaciado = false }) { Text("Cancelar") } }
        )
    }
}
