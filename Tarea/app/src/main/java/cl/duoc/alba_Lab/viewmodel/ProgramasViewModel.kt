package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.data.AlbaLabApi
import cl.duoc.alba_Lab.data.CatalogoWebRepository
import cl.duoc.alba_Lab.data.ProgramaWeb
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProgramasEstado {
    data object Cargando : ProgramasEstado
    data object Error : ProgramasEstado
    data class Disponible(val programas: List<ProgramaWeb>): ProgramasEstado
}

class ProgramasViewModel: ViewModel(){
    private val repositorio = CatalogoWebRepository(AlbaLabApi.catalogo)
    private val _estado = MutableStateFlow<ProgramasEstado>(ProgramasEstado.Cargando)
    val estado = _estado.asStateFlow()
    init {cargar()}

    //consulta los programas al entrar o al tocar reintentar
    fun cargar() {
        _estado.value = ProgramasEstado.Cargando
        viewModelScope.launch {
            try {_estado.value = ProgramasEstado.Disponible(repositorio.obtenerProgramas())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = ProgramasEstado.Error
            }
        }
    }
}