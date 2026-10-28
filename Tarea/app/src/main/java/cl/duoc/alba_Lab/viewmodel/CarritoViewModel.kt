package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.alba_Lab.model.ItemCarrito
import cl.duoc.alba_Lab.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CarritoViewModel: ViewModel() {
    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito = _carrito.asStateFlow()

    //agrega un producto o aumenta cantidad si ya está
    fun agregarProducto(producto: Producto) {
        _carrito.update { lista ->
            if (lista.any { it.producto.id == producto.id }) {
                lista.map { item ->
                    if (item.producto.id == producto.id) {
                        item.copy(cantidad = item.cantidad + 1)
                    } else {
                        item
                    }
                }
            } else {
                lista + ItemCarrito(producto, cantidad = 1)
            }
        }
    }

    //quita una unidad, si esto queda en cero se elimina el producto
    fun quitarUnidad(idProducto: String) {
        _carrito.update { lista ->
            lista.map { item ->
                if (item.producto.id == idProducto) {
                    item.copy(cantidad = item.cantidad - 1)
                } else {
                    item
                }
            }.filter { it.cantidad > 0 }
        }
    }

    fun eliminarProducto(idProducto: String) {
        _carrito.update { lista ->
            lista.filterNot { it.producto.id == idProducto }
        }
    }
    //calculamos le total de referencia del carrito
    fun calcularTotal(): Int {
    return _carrito.value.sumOf{it.subtotal}
    }

    //eliminamos todo
    fun vaciarCarrito(){_carrito.value=emptyList()}
}




