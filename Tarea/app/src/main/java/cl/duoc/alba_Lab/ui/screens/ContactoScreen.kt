package cl.duoc.alba_Lab.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ContactoScreen(navController: NavController) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Contacto", style = MaterialTheme.typography.headlineMedium)
        Text("Consulta los canales oficiales de Alba Lab.")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Correo electrónico", style = MaterialTheme.typography.titleMedium)
                Text("contacto@albaconecta.cl")

                Button(onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:contacto@albaconecta.cl"))
                    //sendto con mailto solicita abrir una app de correo
                        abrirAplicacion(context, intent)} //abre el correo sin enviar ningun mensaje automatico
                ) {
                    Text("Abrir correo")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Teléfono", style = MaterialTheme.typography.titleMedium)
                Text("+56 9 9876 5432")

                Button(onClick = { val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56998765432"))
                    //DIAL abre marcador con el numero listo y el usuario decide si realiza la llamada
                        abrirAplicacion(context, intent)}
                ) {
                    Text("Abrir marcador")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Dirección", style = MaterialTheme.typography.titleMedium)
                Text("General Salvo 20, oficina 110, Providencia.")
                Button(onClick = {val direccion = Uri.encode("General Salvo 20, Providencia, Chile")
                    //se prepara la direccion para incluirla en la uri
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$direccion"))
                    //view con geo pide buscar la direccion en mapas
                        abrirAplicacion(context, intent)}
                ) {
                    Text("Ver ubicación")
                }
            }
        }

        Text(text = "Estos botones abren aplicaciones externas. Tú decides si envías un correo o realizas una llamada.", style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}

//reutilizamos la misma funcion para abrir correo, marcador o mapas
private fun abrirAplicacion(context: Context, intent: Intent) {
    try {context.startActivity(intent) //le pide a android abrir una app compatible con el intent
    } catch (error: ActivityNotFoundException) {
        Toast.makeText(context, "No hay una aplicación disponible para esta acción.",Toast.LENGTH_LONG).show()}
}