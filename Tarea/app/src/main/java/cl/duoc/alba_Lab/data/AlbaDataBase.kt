package cl.duoc.alba_Lab.data

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import cl.duoc.alba_Lab.model.Solicitud
import cl.duoc.alba_Lab.model.Cuenta
import cl.duoc.alba_Lab.model.CarritoGuardado

@Database(
    entities = [Solicitud::class, Cuenta::class, CarritoGuardado::class],
    version = 6,
    exportSchema = false
)
abstract class AlbaDataBase : RoomDatabase() {
    abstract fun carritoDao(): CarritoDao
    abstract fun solicitudDao(): SolicitudDao //permite acceder a las operaciones que creamos
    companion object{
        @Volatile
        private var instancia: AlbaDataBase?= null
        fun obtenerDatabase(context: Context): AlbaDataBase{ //crea la bd una vez y reutiliza la instancia
            return instancia ?: synchronized(this){
                val nuevaInstancia= Room.databaseBuilder(context.applicationContext, AlbaDataBase::class.java,"alba_database")
                    // Como en el ejemplo: recrea las tablas si cambia la versión sin migración.
                    .fallbackToDestructiveMigration().build()
                instancia = nuevaInstancia
                nuevaInstancia
            }
        }
    }

}
