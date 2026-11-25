# Alba Conecta

Aplicación móvil desarrollada para la asignatura Desarrollo de Aplicaciones Móviles de Duoc UC.

Su finalidad es reunir información de CORPALIV y Alba Lab y facilitar que personas y empresas registren su interés en colaborar mediante donaciones, voluntariado, alianzas o consultas por productos y servicios.

## Backend

La aplicación se conecta a un backend desarrollado con Spring Boot, disponible en el repositorio [Alba Catálogo API](https://github.com/Priss-x/Alba-catalogo-api).

La API permite gestionar cuentas y solicitudes, y consultar productos y programas. La aplicación utiliza la dirección `https://alba-catalogo-api.onrender.com/`.

## Integrantes

- José Santander
- Priscila Contreras
- Grupo 10

## Funcionalidades actuales

- Registro de personas y empresas, inicio de sesión mediante la API y edición del perfil.
- Foto de perfil y archivos de imagen adjuntos desde la galería.
- Creación de solicitudes con cuenta o como invitado, con validaciones y resumen.
- Consulta, edición y eliminación de solicitudes propias e historial general de depuración.
- Catálogo y carrito separado por cuenta, con cantidades y total de referencia.
- Información de programas y contacto, menú lateral y selección de tema.
- Almacenamiento local de datos de cuentas, copias de solicitudes y carritos, y pantallas adaptables con desplazamiento.

## Tecnologías

Kotlin, Jetpack Compose, Material 3 y Navigation Compose. ViewModel y StateFlow para gestionar el estado, Retrofit y Gson para consumir la API, Room (SQLite) para los datos locales y archivos internos para las imágenes.

## Ejecución

Abrir la carpeta `Tarea` en Android Studio, sincronizar Gradle y ejecutar la aplicación en un emulador o dispositivo con Android 7.0 (API 24) o superior. Las funciones que utilizan la API requieren conexión a Internet y que el backend esté disponible.

## Pruebas

Las pruebas locales de `Tarea/app/src/test` utilizan Kotest, MockK y MockWebServer para comprobar la lógica de la aplicación y algunos contratos HTTP, como la consulta de productos y la edición de solicitudes. Utilizan respuestas simuladas; no verifican la disponibilidad ni el funcionamiento del backend desplegado.

Las pruebas de interfaz se encuentran en `Tarea/app/src/androidTest` y requieren un emulador o dispositivo Android.

---
Proyecto académico en desarrollo. El inicio de sesión y el envío de solicitudes se realizan mediante el backend del proyecto; esto no implica una gestión efectiva por parte de la organización. No se realizan pagos. Se utilizan datos ficticios para las pruebas.
