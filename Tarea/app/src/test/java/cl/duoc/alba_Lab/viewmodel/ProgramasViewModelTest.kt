package cl.duoc.alba_Lab.viewmodel

import cl.duoc.alba_Lab.PruebaViewModel
import cl.duoc.alba_Lab.data.CatalogoWebRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException

class ProgramasViewModelTest : PruebaViewModel() {
    init {

        test("un error de programas permite volver a consultar") {
            val repository = mockk<CatalogoWebRepository>()
            coEvery { repository.obtenerProgramas() } throws IOException()
            val viewModel = registrar(ProgramasViewModel(repository))
            completarTareas()
            viewModel.estado.value shouldBe ProgramasEstado.Error

            coEvery { repository.obtenerProgramas() } returns emptyList()
            viewModel.cargar()
            completarTareas()
            viewModel.estado.value shouldBe ProgramasEstado.Disponible(emptyList())
        }
    }
}

