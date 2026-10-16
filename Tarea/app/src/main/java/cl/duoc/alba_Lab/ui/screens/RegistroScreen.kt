package cl.duoc.alba_Lab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.alba_Lab.navigation.Screen
import cl.duoc.alba_Lab.viewmodel.UsuarioViewModel

// Registro: formulario que observa el estado y muestra los errores del ViewModel.
@Composable
fun RegistroScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()

    // Desplazamiento: permite acceder a todos los campos cuando aparece el teclado.
    Column(
        modifier = Modifier
            .fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Quiero colaborar", style = MaterialTheme.typography.headlineMedium)

        // Nombre: campo obligatorio con mensaje de error debajo.
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre") },
            isError = estado.errores.nombre != null,
            supportingText = { estado.errores.nombre?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Correo: usa el teclado de correo y muestra su validación.
        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = estado.errores.correo != null,
            supportingText = { estado.errores.correo?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Contraseña: oculta visualmente los caracteres ingresados.
        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = estado.errores.clave != null,
            supportingText = { estado.errores.clave?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        //Seleccionamos el tipo de colaborador (persona o empresa)
        Text(text = "¿Quién desea colaborar?", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically){
            RadioButton(selected = estado.tipoColaborador == "Persona",
                        onClick = {
                            viewModel.onTipoColaboradorChange("Persona")
                        }
            )
            Text("Persona")
            Spacer(modifier= Modifier.width(16.dp))
            RadioButton(selected = estado.tipoColaborador =="Empresa",
                        onClick = {
                            viewModel.onTipoColaboradorChange("Empresa")
                        }
            )
            Text("Empresa")
        }

        // Dirección: campo obligatorio del formulario.
        OutlinedTextField(
            value = estado.direccion,
            onValueChange = viewModel::onDireccionChange,
            label = { Text("Dirección") },
            isError = estado.errores.direccion != null,
            supportingText = { estado.errores.direccion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        //Muestro el error si llegase a faltar seleccionar una opcion
        estado.errores.tipoColaborador?.let{mensaje -> Text(text= mensaje, color= MaterialTheme.colorScheme.error)}
        if (estado.tipoColaborador == "Empresa"){OutlinedTextField(
                value = estado.empresa,
                onValueChange = viewModel::onEmpresaChange,
                label = { Text("Nombre de la empresa") },
                isError = estado.errores.empresa != null,
                supportingText = {estado.errores.empresa?.let { Text(it) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Checkbox: guarda la aceptación de términos para mostrarla en Resumen.
        Text(text = "¿Cómo deseas colaborar?",style = MaterialTheme.typography.titleMedium)

        val modalidades = listOf("Donación",
                                 "Voluntariado",
                                 "Alianza empresarial",
                                 "Consulta por productos y servicios")

        modalidades.forEach {opcion ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                RadioButton(selected = estado.modalidad == opcion,
                            onClick = {viewModel.onModalidadChange(opcion) }
                )
                Text(text = opcion,
                    modifier = Modifier.weight(1f))
            }
        }

        estado.errores.modalidad?.let {mensaje ->
            Text(text = mensaje,color = MaterialTheme.colorScheme.error)
        }
        //Mensaje de la persona interesada en donar
        OutlinedTextField(value = estado.mensaje,
                         onValueChange = viewModel::onMensajeChange,
                         label = { Text("Mensaje") },
                         placeholder = {Text("Cuéntanos cómo te gustaría colaborar")
            },
            isError = estado.errores.mensaje != null,
            supportingText = {estado.errores.mensaje?.let { Text(it) }
            },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = estado.aceptaTerminos,
                onCheckedChange = viewModel::onAceptarTerminosChange
            )
            Spacer(Modifier.width(8.dp))
            Text("Acepto los términos y condiciones")
        }

        // Registrar: valida todos los campos antes de navegar al resumen.
        Button(
            onClick = {
                if (viewModel.validarFormulario()) {
                    navController.navigate(Screen.Resumen.route) { launchSingleTop = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
