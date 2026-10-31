# Alba Conecta

Aplicación móvil desarrollada para la asignatura Desarrollo de Aplicaciones Móviles de Duoc UC.

Su finalidad es reunir información de CORPALIV y Alba Lab y facilitar que personas y empresas registren su interés en colaborar mediante donaciones, voluntariado, alianzas o consultas por productos y servicios.

## Integrantes

- José Santander
- Priscila Contreras
- Grupo 10

## Funcionalidades actuales

- Registro de personas y empresas, inicio de sesión simulado y edición del perfil.
- Foto de perfil y archivos de imagen adjuntos desde la galería.
- Creación de solicitudes con cuenta o como invitado, con validaciones y resumen.
- Consulta, edición y eliminación de solicitudes propias e historial general de depuración.
- Catálogo y carrito separado por cuenta, con cantidades y total de referencia.
- Información de programas y contacto, menú lateral y selección de tema.
- Almacenamiento local de cuentas, solicitudes y carritos, y pantallas adaptables con desplazamiento.

## Tecnologías

Kotlin, Jetpack Compose, Material 3 y Navigation Compose. ViewModel y StateFlow para gestionar el estado, Room (SQLite) para los datos locales y archivos internos para las imágenes.

## Ejecución

Abrir la carpeta `Tarea` en Android Studio, sincronizar Gradle y ejecutar la aplicación en un emulador o dispositivo Android compatible.

---
Proyecto académico en desarrollo. El login es una simulación local; no se realizan pagos ni se envían solicitudes reales a la organización. Se utilizan datos ficticios para las pruebas.
