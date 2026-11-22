package cl.duoc.alba_Lab.viewmodel

import cl.duoc.alba_Lab.PruebaViewModel
import cl.duoc.alba_Lab.data.CatalogoWebRepository
import cl.duoc.alba_Lab.data.ProductoWeb
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException

class CatalogoWebViewModelTest : PruebaViewModel() {
    init {
        test("al cargar pasa de inicial a cargando y luego muestra los productos") {
            val repository = mockk<CatalogoWebRepository>()
            val productos = listOf(ProductoWeb(1, "Difusor", "Aroma suave", 7500))
            coEvery { repository.obtenerProductos() } returns productos
            val viewModel = registrar(CatalogoWebViewModel(repository))

            viewModel.estado.value shouldBe CatalogoWebEstado.Inicial
            viewModel.cargar()
            viewModel.estado.value shouldBe CatalogoWebEstado.Cargando
            completarTareas()
            viewModel.estado.value shouldBe CatalogoWebEstado.Disponible(productos)
        }

        test("un fallo de conexion muestra error y permite reintentar") {
            val repository = mockk<CatalogoWebRepository>()
            coEvery { repository.obtenerProductos() } throws IOException("sin conexión")
            val viewModel = registrar(CatalogoWebViewModel(repository))
            viewModel.cargar()
            completarTareas()
            viewModel.estado.value shouldBe CatalogoWebEstado.Error

            coEvery { repository.obtenerProductos() } returns emptyList()
            viewModel.cargar()
            completarTareas()
            viewModel.estado.value shouldBe CatalogoWebEstado.Disponible(emptyList())
        }

    }
}

