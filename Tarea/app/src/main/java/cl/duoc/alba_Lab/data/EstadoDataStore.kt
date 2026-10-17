package cl.duoc.alba_Lab.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//se declara fuera de la clase para compartir una sola instancia
private val Context.preferenciasAlba by preferencesDataStore(name = "preferencias_alba")

class EstadoDataStore(context: Context) {
    private val contexto = context.applicationContext

    //Identifica la preferencia dentro del archivvo
    private val modoActivado = booleanPreferencesKey(name = "modo_colaboracion_activado")

    //Aqui lee la preferencia, osea sii todavía no existe, devuelve false
    fun obtenerEstado(): Flow<Boolean> {
        return contexto.preferenciasAlba.data.map { preferencias -> preferencias[modoActivado] ?: false}
    }

    //Guarda la preferencia en el almacenamiento del dispo
    suspend fun guardarEstado(activo: Boolean) {
        contexto.preferenciasAlba.edit { preferencias -> preferencias[modoActivado] = activo}
    }
}