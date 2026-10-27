package cl.duoc.alba_Lab.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Copia privada y pequeña: no depende del permiso temporal de la galería.
class FotoPerfilStorage(private val context: Context) {
    private fun archivo(id: String) = File(context.filesDir, "perfil_$id.jpg")

    suspend fun cargar(id: String): Bitmap? = withContext(Dispatchers.IO) {
        BitmapFactory.decodeFile(archivo(id).absolutePath)
    }

    suspend fun guardar(id: String, uri: Uri) = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val imagen = if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri)) { decoder, info, _ ->
                val escala = minOf(1f, 512f / maxOf(info.size.width, info.size.height))
                decoder.setTargetSize(maxOf(1, (info.size.width * escala).toInt()),
                    maxOf(1, (info.size.height * escala).toInt()))
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
            require(opciones.outWidth > 0 && opciones.outHeight > 0)
            opciones.inSampleSize = 1
            while (maxOf(opciones.outWidth, opciones.outHeight) / opciones.inSampleSize > 512) {
                opciones.inSampleSize *= 2
            }
            opciones.inJustDecodeBounds = false
            requireNotNull(resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) })
        }
        val temporal = File.createTempFile("foto_perfil_", ".jpg", context.filesDir)
        try {
            temporal.outputStream().use { check(imagen.compress(Bitmap.CompressFormat.JPEG, 90, it)) }
            check(temporal.renameTo(archivo(id)))
        } finally {
            imagen.recycle()
            temporal.delete()
        }
    }
}
