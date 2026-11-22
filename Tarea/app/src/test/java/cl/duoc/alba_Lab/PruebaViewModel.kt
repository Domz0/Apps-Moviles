package cl.duoc.alba_Lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
abstract class PruebaViewModel : FunSpec() {
    private val almacen = ViewModelStore()
    protected val dispatcher = StandardTestDispatcher()

    init {
        beforeTest { Dispatchers.setMain(dispatcher) }
        afterTest {
            almacen.clear()
            dispatcher.scheduler.advanceUntilIdle()
            Dispatchers.resetMain()
        }
    }

    protected fun <T : ViewModel> registrar(viewModel: T): T {
        almacen.put(viewModel.javaClass.name, viewModel)
        return viewModel
    }

    protected fun completarTareas() = dispatcher.scheduler.advanceUntilIdle()
}

