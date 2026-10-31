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
    abstract fun cuentaDao(): CuentaDao //cuentas creadas
    abstract fun carritoDao(): CarritoDao // lo que hay en el carrito
    abstract fun solicitudDao(): SolicitudDao // maneja solicitudes
    companion object{
        @Volatile //nos permite acceder desde cualquier hilo
        private var instancia: AlbaDataBase?= null
        fun obtenerDatabase(context: Context): AlbaDataBase{ //crea la bd una vez y reutiliza la instancia
            return instancia ?: synchronized(this){
                val nuevaInstancia= Room.databaseBuilder(context.applicationContext,
                    AlbaDataBase::class.java,"alba_database")
                    .fallbackToDestructiveMigration().build()
                instancia = nuevaInstancia
                nuevaInstancia
            }
        }
    }

}
