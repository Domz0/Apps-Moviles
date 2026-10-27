package cl.duoc.alba_Lab.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ImagenInteligente(imagen: Bitmap?) {
    val modifier = Modifier.size(128.dp).clip(CircleShape)
    if (imagen == null) {
        Icon(Icons.Default.Person, contentDescription = "Sin foto de perfil", modifier = modifier)
    } else {
        Image(imagen.asImageBitmap(), contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop, modifier = modifier)
    }
}
