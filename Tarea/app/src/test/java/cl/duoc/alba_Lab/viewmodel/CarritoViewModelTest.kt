package cl.duoc.alba_Lab.viewmodel

import cl.duoc.alba_Lab.PruebaViewModel
import cl.duoc.alba_Lab.data.CarritoRepository
import cl.duoc.alba_Lab.data.CatalogoProductos
import cl.duoc.alba_Lab.model.CarritoGuardado
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow

class CarritoViewModelTest : PruebaViewModel() {
    private lateinit var repository: CarritoRepository
    private lateinit var guardados: MutableStateFlow<List<CarritoGuardado>>
    private lateinit var viewModel: CarritoViewModel
    private val producto = CatalogoProductos.elementos.first()

    init {
        beforeTest {
            repository = mockk(relaxed = true)
            guardados = MutableStateFlow(emptyList())
            every { repository.obtenerCarrito(any()) } returns guardados
            coEvery { repository.buscarProducto(any(), any()) } returns null
            viewModel = registrar(CarritoViewModel(repository, "cuenta-1"))
            completarTareas()
        }

        test("agregar un producto existente aumenta su cantidad") {
            coEvery { repository.buscarProducto("cuenta-1", producto.id) } returns CarritoGuardado("cuenta-1", producto.id, 2)
            viewModel.agregarProducto(producto)
            completarTareas()
            coVerify { repository.guardar(CarritoGuardado("cuenta-1", producto.id, 3)) }
        }

        test("quitar la última unidad elimina el producto") {
            coEvery { repository.buscarProducto("cuenta-1", producto.id) } returns CarritoGuardado("cuenta-1", producto.id, 1)
            viewModel.quitarUnidad(producto.id)
            completarTareas()
            coVerify { repository.eliminarProducto("cuenta-1", producto.id) }
        }

        test("el total suma precio por cantidad y omite productos desconocidos") {
            guardados.value = listOf(
                CarritoGuardado("cuenta-1", producto.id, 3),
                CarritoGuardado("cuenta-1", "producto-inexistente", 9)
            )
            completarTareas()
            viewModel.calcularTotal() shouldBe producto.precio * 3
            viewModel.carrito.value.size shouldBe 1
        }

    }
}

