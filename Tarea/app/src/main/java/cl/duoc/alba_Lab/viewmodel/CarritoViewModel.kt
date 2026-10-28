package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.data.CarritoRepository
import cl.duoc.alba_Lab.data.CatalogoProductos
import cl.duoc.alba_Lab.model.CarritoGuardado
import cl.duoc.alba_Lab.model.ItemCarrito
import cl.duoc.alba_Lab.model.Producto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// corresponde a cada usuario
class CarritoViewModel(private val repository: CarritoRepository, private val cuentaId: String?) : ViewModel() {
    val conSesion = cuentaId != null
    val carrito = repository.obtenerCarrito(cuentaId.orEmpty()).map { guardados ->
        guardados.mapNotNull { item ->
            CatalogoProductos.elementos.find { it.id == item.productoId }?.let { producto ->
                ItemCarrito(producto, item.cantidad)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _procesando = MutableStateFlow(false)
    val procesando = _procesando.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Evita dos cambios simultáneos de cantidad mientras se guarda en Room.
    private fun modificar(operacion: suspend (String) -> Unit) {
        val id = cuentaId ?: return
        if (_procesando.value) return
        _procesando.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                operacion(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar el carrito. Intenta nuevamente."
            } finally {
                _procesando.value = false
            }
        }
    }
    fun agregarProducto(producto: Producto) = modificar { id ->
        val actual = repository.buscarProducto(id, producto.id)
        repository.guardar(CarritoGuardado(id, producto.id, (actual?.cantidad ?: 0) + 1))
    }
    fun quitarUnidad(productoId: String) = modificar { id ->
        val actual = repository.buscarProducto(id, productoId)
        if (actual != null) {
            if (actual.cantidad > 1) repository.guardar(actual.copy(cantidad = actual.cantidad - 1))
            else repository.eliminarProducto(id, productoId)
        }
    }
    fun eliminarProducto(productoId: String) = modificar { id -> repository.eliminarProducto(id, productoId) }
    fun vaciarCarrito() = modificar { id -> repository.vaciarCarrito(id) }
    fun calcularTotal(): Int = carrito.value.sumOf { it.subtotal }
}