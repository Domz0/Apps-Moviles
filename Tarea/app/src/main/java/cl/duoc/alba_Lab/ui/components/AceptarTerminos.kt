package cl.duoc.alba_Lab.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Composable
fun AceptarTerminos(aceptados: Boolean, error: String?, habilitado: Boolean, onCambiar: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(
            value = aceptados, enabled = habilitado, role = Role.Checkbox, onValueChange = onCambiar
        ), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = aceptados, onCheckedChange = null, enabled = habilitado)
        Text("Acepto los términos y condiciones")
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}
