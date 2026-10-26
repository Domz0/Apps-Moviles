package cl.duoc.alba_Lab.data

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import cl.duoc.alba_Lab.model.Solicitud

@Database(
    entities = [Solicitud::class],
    version = 1, //primera version de escritura de la base
    exportSchema = false
)
abstract class AlbaDataBase : RoomDatabase() {
    abstract fun solicitudDao(): SolicitudDao //permite acceder a las operaciones que creamos
    companion object{
        @Volatile
        private var instancia: AlbaDataBase?= null
        fun obtenerDatabase(context: Context): AlbaDataBase{ //crea la bd una vez y reutiliza la instancia
            return instancia ?: synchronized(this){
                val nuevaInstancia= Room.databaseBuilder(context.applicationContext, AlbaDataBase::class.java,"alba_database").build()
                instancia = nuevaInstancia
                nuevaInstancia
            }
        }
    }

}