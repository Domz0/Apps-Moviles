package cl.duoc.alba_Lab.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// CARGA, GUARDA, ELIMINA
// se usa para carga de imagen en general, en solicitudes como en cambio de perfil
class ImagenStorage(private val context: Context, private val prefijo: String = "perfil_") {
    // contruye File
    private fun archivo(id: String) = File(context.filesDir, "$prefijo$id.jpg")
    // elimina a base de una id
    suspend fun eliminar(id: String) = withContext(Dispatchers.IO) { archivo(id).delete() }

    suspend fun cargar(id: String): Bitmap? = withContext(Dispatchers.IO) {
        val ruta = archivo(id).absolutePath
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, opciones)
        // Lee una vista pequeña para no llenar la memoria con fotos grandes
        opciones.inSampleSize = 1
        while (maxOf(opciones.outWidth, opciones.outHeight) / opciones.inSampleSize > 512) {
            opciones.inSampleSize *= 2 // aplica la reduccion
        }
        opciones.inJustDecodeBounds = false
        BitmapFactory.decodeFile(ruta, opciones)
    }

    //Uri corresponde al recurso identificado
    suspend fun guardar(id: String, uri: Uri) = withContext(Dispatchers.IO) {
        // generamos un archivo temporal como intermediario
        val temporal = File.createTempFile("foto_", ".tmp", context.filesDir)
        try {
            val entrada = requireNotNull(context.contentResolver.openInputStream(uri))

            entrada.use { origen -> temporal.outputStream().use { destino -> origen.copyTo(destino) } }
            val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(temporal.absolutePath, opciones)
            // comprobamos que sea una imagen de 1 x1 en adelante
            require(opciones.outWidth > 0 && opciones.outHeight > 0) { "Imagen no válida" }
            // se intenta mover o renombrar el
            check(temporal.renameTo(archivo(id)))
        } finally {
            temporal.delete()
        }
    }
}
