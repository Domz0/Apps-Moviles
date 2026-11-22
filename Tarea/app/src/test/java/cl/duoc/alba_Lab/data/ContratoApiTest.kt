package cl.duoc.alba_Lab.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ContratoApiTest : FunSpec({
    lateinit var servidor: MockWebServer
    lateinit var catalogo: CatalogoWebApi
    lateinit var cuentas: CuentasWebApi

    beforeTest {
        servidor = MockWebServer()
        servidor.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(servidor.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        catalogo = retrofit.create(CatalogoWebApi::class.java)
        cuentas = retrofit.create(CuentasWebApi::class.java)
    }

    afterTest { servidor.shutdown() }

    test("retrofit consulta productos y convierte el json recibido") {
        servidor.enqueue(MockResponse().setBody("""[{"id":1,"nombre":"Difusor","descripcion":"Aroma","precio":8500}]"""))
        catalogo.obtenerProductos() shouldBe listOf(ProductoWeb(1, "Difusor", "Aroma", 8500))
        val peticion = requireNotNull(servidor.takeRequest(1, TimeUnit.SECONDS))
        peticion.method shouldBe "GET"
        peticion.path shouldBe "/api/productos"
    }

    test("editar una solicitud envía el id y las cabeceras de seguridad") {
        servidor.enqueue(MockResponse().setBody("""{"id":8,"modalidad":"Donación","mensaje":"Cambio"}"""))
        cuentas.editarSolicitud(8, "Basic prueba", mapOf("X-CSRF-TOKEN" to "token"), DatosSolicitudWeb("Donación", "Cambio"))
        val peticion = requireNotNull(servidor.takeRequest(1, TimeUnit.SECONDS))
        peticion.method shouldBe "PUT"
        peticion.path shouldBe "/api/solicitudes/8"
        peticion.getHeader("Authorization") shouldBe "Basic prueba"
        peticion.getHeader("X-CSRF-TOKEN") shouldBe "token"
        JSONObject(peticion.body.readUtf8()).getString("mensaje") shouldBe "Cambio"
    }

})

