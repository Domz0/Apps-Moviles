package cl.duoc.alba_Lab.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import cl.duoc.alba_Lab.model.UsuarioUiState
import cl.duoc.alba_Lab.ui.components.DatosPersonales
import org.junit.Rule
import org.junit.Test

class FormulariosUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun losCamposSeBloqueanMientrasSeProcesa() {
        compose.setContent {
            MaterialTheme {
                Column {
                    DatosPersonales(UsuarioUiState(procesando = true), true, {}, {}, {}, {})
                }
            }
        }
        compose.onNodeWithText("Nombre").assertIsNotEnabled()
        compose.onNodeWithText("Correo electrónico").assertIsNotEnabled()
        compose.onNodeWithText("Contraseña").assertIsNotEnabled()
    }

}

