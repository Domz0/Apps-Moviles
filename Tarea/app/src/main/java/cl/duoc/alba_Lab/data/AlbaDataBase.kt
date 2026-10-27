package cl.duoc.alba_Lab.data

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.alba_Lab.model.Solicitud
import cl.duoc.alba_Lab.model.Cuenta

@Database(
    entities = [Solicitud::class, Cuenta::class],
    version = 4,
    exportSchema = false
)
abstract class AlbaDataBase : RoomDatabase() {
    abstract fun solicitudDao(): SolicitudDao //permite acceder a las operaciones que creamos
    companion object{
        // Agrega los nuevos campos sin borrar las cuentas ya registradas.
        val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cuentas ADD COLUMN direccion TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cuentas ADD COLUMN aceptaTerminos INTEGER NOT NULL DEFAULT 0")
            }
        }
        @Volatile
        private var instancia: AlbaDataBase?= null
        fun obtenerDatabase(context: Context): AlbaDataBase{ //crea la bd una vez y reutiliza la instancia
            return instancia ?: synchronized(this){
                val nuevaInstancia= Room.databaseBuilder(context.applicationContext, AlbaDataBase::class.java,"alba_database")
                    .addMigrations(MIGRACION_3_4).build()
                instancia = nuevaInstancia
                nuevaInstancia
            }
        }
    }

}
