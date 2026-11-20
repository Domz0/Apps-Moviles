package cl.duoc.alba_Lab.data

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//conservamos la sesión de la apipi en memoria mientras usamos la app
class ClienteCuentasWeb {
    private val cookiesSesion = CookiesSesion()
    private val cliente = OkHttpClient.Builder()
        .cookieJar(cookiesSesion)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()

    val api: CuentasWebApi = Retrofit.Builder()
        .baseUrl("https://alba-catalogo-api.onrender.com/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CuentasWebApi::class.java)

    fun limpiarSesion() {cookiesSesion.limpiar()}
    private class CookiesSesion: CookieJar {private val guardadas= mutableListOf<Cookie>()
        @Synchronized
        override fun saveFromResponse(url: HttpUrl,cookies: List<Cookie>){for (cookie in cookies) {
                guardadas.removeAll {it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path}
                if (cookie.expiresAt > System.currentTimeMillis()) {guardadas.add(cookie)}}
        }
        @Synchronized
        override fun loadForRequest(url: HttpUrl): List<Cookie>{
            guardadas.removeAll {it.expiresAt <= System.currentTimeMillis()}
            return guardadas.filter { it.matches(url) }
        }
        @Synchronized
        fun limpiar() {guardadas.clear()}
    }
}