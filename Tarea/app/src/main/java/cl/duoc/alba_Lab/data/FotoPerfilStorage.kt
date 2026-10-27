package cl.duoc.alba_Lab.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Copia local: no depende del permiso temporal de la galería
class FotoPerfilStorage(private val context: Context, private val prefijo: String = "perfil_") {
    private fun archivo(id: String) = File(context.filesDir, "$prefijo$id.jpg")

    suspend fun eliminar(id: String) = withContext(Dispatchers.IO) { archivo(id).delete() }

    suspend fun cargar(id: String): Bitmap? = withContext(Dispatchers.IO) {
        val ruta = archivo(id).absolutePath
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, opciones)
        // Lee una vista pequeña para no llenar la memoria con fotos grandes.
        opciones.inSampleSize = 1
        while (maxOf(opciones.outWidth, opciones.outHeight) / opciones.inSampleSize > 512) {
            opciones.inSampleSize *= 2
        }
        opciones.inJustDecodeBounds = false
        BitmapFactory.decodeFile(ruta, opciones)
    }

    suspend fun guardar(id: String, uri: Uri) = withContext(Dispatchers.IO) {
        val temporal = File.createTempFile("foto_", ".tmp", context.filesDir)
        try {
            val entrada = requireNotNull(context.contentResolver.openInputStream(uri))
            entrada.use { origen -> temporal.outputStream().use { destino -> origen.copyTo(destino) } }
            val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(temporal.absolutePath, opciones)
            require(opciones.outWidth > 0 && opciones.outHeight > 0) { "Imagen no válida" }
            // Reemplaza la foto solamente cuando la copia terminó correctamente
            check(temporal.renameTo(archivo(id)))
        } finally {
            temporal.delete()
        }
    }
}
