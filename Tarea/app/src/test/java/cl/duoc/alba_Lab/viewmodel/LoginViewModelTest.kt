package cl.duoc.alba_Lab.viewmodel

import cl.duoc.alba_Lab.PruebaViewModel
import io.kotest.matchers.shouldBe

class LoginViewModelTest : PruebaViewModel() {
    init {
        test("un formulario vacio muestra los dos campos obligatorios") {
            val viewModel = registrar(LoginViewModel())
            viewModel.validarFormulario()
            viewModel.estado.value.errorCorreo shouldBe "Ingresa tu correo electrónico"
            viewModel.estado.value.errorClave shouldBe "Ingresa tu contraseña"
            viewModel.estado.value.datosValidos shouldBe false
        }

        test("un correo valido pierde los espacios de los extremos") {
            val viewModel = registrar(LoginViewModel())
            viewModel.onCorreoChange("  test@duoc.cl  ")
            viewModel.onClaveChange("secreto")
            viewModel.validarFormulario()
            viewModel.estado.value.correo shouldBe "test@duoc.cl"
            viewModel.estado.value.datosValidos shouldBe true
        }

    }
}

