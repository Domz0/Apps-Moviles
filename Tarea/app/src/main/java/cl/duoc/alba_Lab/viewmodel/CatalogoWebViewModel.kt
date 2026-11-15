package cl.duoc.alba_Lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.alba_Lab.data.CatalogoWebRepository
import cl.duoc.alba_Lab.data.ProductoWeb
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CatalogoWebEstado{
    data object Inicial: CatalogoWebEstado
    data object Cargando: CatalogoWebEstado
    data class Disponible(val productos: List<ProductoWeb>): CatalogoWebEstado
    data object Error: CatalogoWebEstado
}
class CatalogoWebViewModel(private val repository: CatalogoWebRepository
): ViewModel() {
    private val _estado= MutableStateFlow<CatalogoWebEstado>(CatalogoWebEstado.Inicial)
    val estado = _estado.asStateFlow()

    fun cargar(){
        if(_estado.value== CatalogoWebEstado.Cargando) return
        _estado.value= CatalogoWebEstado.Cargando
        viewModelScope.launch{
            try{
                val productos= repository.obtenerProductos()
                _estado.value= CatalogoWebEstado.Disponible(productos)
            }catch (e: CancellationException){
                throw e
            } catch (e: Exception){_estado.value= CatalogoWebEstado.Error
            }
        }
    }
}