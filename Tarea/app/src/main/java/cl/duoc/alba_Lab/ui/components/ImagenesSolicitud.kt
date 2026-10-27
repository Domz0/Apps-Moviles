package cl.duoc.alba_Lab.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.alba_Lab.data.FotoPerfilStorage

@Composable
fun ImagenesSolicitud(imagenes: List<String>, onQuitar: ((String) -> Unit)? = null) {
    val context = LocalContext.current.applicationContext
    val storage = remember(context) { FotoPerfilStorage(context, "solicitud_") }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(imagenes, key = { it }) { id ->
            val bitmap by produceState<Bitmap?>(null, id) { value = storage.cargar(id) }
            Column {
                val imagen = bitmap
                if (imagen != null) {
                    Image(imagen.asImageBitmap(), "Imagen adjunta", Modifier.size(120.dp), contentScale = ContentScale.Crop)
                } else {
                    Text("Imagen no disponible", modifier = Modifier.width(120.dp))
                }
                if (onQuitar != null) TextButton(onClick = { onQuitar(id) }) { Text("Quitar") }
            }
        }
    }
}
