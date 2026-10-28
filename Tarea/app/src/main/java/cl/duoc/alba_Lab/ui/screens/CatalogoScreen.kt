package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.R
import cl.duoc.alba_Lab.viewmodel.MainViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cl.duoc.alba_Lab.model.Producto
import cl.duoc.alba_Lab.viewmodel.CarritoViewModel
import androidx.compose.material3.Button

@Composable
fun CatalogoScreen(viewModel: MainViewModel, carritoViewModel: CarritoViewModel) {
    val carrito by carritoViewModel.carrito.collectAsState()
    val elementos = listOf(
        Producto("difusor_aroma_100", "Difusor de aroma 100 ml",R.drawable.difusor_aroma, "Aromatizador de ambiente en formato spray, en envase PET de 100 ml. Aromas publicados: Alba, coco lavanda, vainilla y verbena limón.", precio = 7500),
        Producto("difusor_colgante", "Difusor colgante",R.drawable.difusor_colgante, "Aromatizador de 8 ml con piedras naturales y envase de vidrio. Pensado para espacios pequeños, como el auto o el clóset. Aromas publicados: Alba, coco lavanda, vainilla y verbena limón.", precio = 7500))
    Muestra("Catálogo") {
        Text(text = "Productos con propósito", style= MaterialTheme.typography.titleLarge)
        Text(text= "Conoce productos elaborados por jóvenes y adultos de Alba Lab, en un espacio que impulsa sus habilidades y oportunidades.")
        Text(text="¿Quieres ayudar? Elige 'Crear solicitud' desde Mi perfil o 'Colaborar como invitado' en el menú.")
        Text(text = "Catálogo de referencia para fines académicos. Las solicitudes de esta app son demostrativas y no se envían a CORPALIV.", style = MaterialTheme.typography.bodySmall)
        // serian las noticias o colabs
        TextButton(onClick = { viewModel.navigateBack() }) { Text("Volver") }
        Text(text = "Unidades en el carrito: ${carrito.sumOf { it.cantidad }}", style= MaterialTheme.typography.titleMedium)
        Text(text= "Total presupuestado: \$${carritoViewModel.calcularTotal()}", style= MaterialTheme.typography.titleLarge)
        elementos.forEach { producto ->
            val nombre = producto.nombre
            val imagen= producto.imagen
            val descripcion = producto.descripcion
            var mostrarDetalles by rememberSaveable {mutableStateOf(false)}
            Card(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(imagen),
                        contentDescription = nombre,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = {mostrarDetalles = !mostrarDetalles} ){
                        Text(if(mostrarDetalles)"Ocultar detalles" else "Ver detalles")
                    }
                    AnimatedVisibility(visible= mostrarDetalles) {
                        Text(text = descripcion)
                    }
                    Text(text = "Precio de referencia: \$${producto.precio}")
                    Button(onClick = {carritoViewModel.agregarProducto(producto)},
                        modifier = Modifier.fillMaxWidth()
                    ){Text("Agregar al carrito")}
                    val cantidad = carrito.find{it.producto.id == producto.id} ?.cantidad ?:0
                    if (cantidad>0) {
                        Text(text = "En el carrito: $cantidad")
                        TextButton(onClick = { carritoViewModel.quitarUnidad(producto.id) }
                        ) { Text("Quitar una unidad") }
                        TextButton(onClick = {
                            carritoViewModel.eliminarProducto(producto.id)
                        }
                        ) { Text("Eliminar del carrito") }
                    }

                }
            }
        }
    }
}
@Composable
private fun Muestra(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    MaterialTheme { Surface { Marco(titulo, contenido) } }
}
@Composable
private fun Marco(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        contenido()
    }
}
