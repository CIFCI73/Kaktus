# 🌵 KAKTUS - Gran Canaria Events
Cristian Romeo y Riccardo Belletti

### 1. Introducción y Contexto del Proyecto

El proyecto Kaktus nace como respuesta a una necesidad real detectada durante nuestra experiencia académica y vital en la isla de Gran Canaria. A pesar de la enorme oferta cultural, deportiva y de ocio que ofrece la isla, nos dimos cuenta de que la información suele estar muy fragmentada entre diferentes páginas web, carteles físicos y redes sociales. Para un estudiante Erasmus o un turista recién llegado, resulta complicado tener una visión clara de "qué hacer hoy" sin tener que consultar múltiples fuentes.

Nuestra propuesta, Kaktus, es una aplicación nativa para Android desarrollada íntegramente en Kotlin. Se trata de una plataforma centralizada y social diseñada para conectar a las personas con las actividades locales. La aplicación no solo funciona como un tablón de anuncios digital, sino que fomenta la interacción de la comunidad permitiendo a los usuarios votar sus eventos favoritos y contribuir al ecosistema subiendo sus propias propuestas.

Desde el punto de vista del diseño, hemos querido alejarnos de las interfaces genéricas. La identidad visual de la aplicación está profundamente inspirada en el entorno natural de Canarias, utilizando una paleta de colores basada en el verde de los cactus y el tono beige de la arena, buscando transmitir calidez y simplicidad al usuario final.

### 2. Stack Tecnológico (Herramientas y Librerías)
Para garantizar que Kaktus sea una aplicación moderna, eficiente y mantenible, hemos seleccionado cuidadosamente las tecnologías más recomendadas por Google para el desarrollo Android actual.

El núcleo de la aplicación está construido sobre Kotlin, aprovechando sus características de seguridad (como la gestión de nulos) y su sintaxis concisa. Para la interfaz de usuario, utilizamos Jetpack Compose junto con los componentes de Material Design 3. Esto nos ha permitido crear pantallas dinámicas (Scaffold, LazyColumn, Cards) escribiendo exclusivamente código Kotlin, sin necesidad de archivos de diseño externos.

Para la infraestructura de backend ("Backend as a Service"), confiamos plenamente en el ecosistema de Firebase. Específicamente, utilizamos Firebase Authentication para gestionar el registro y login de usuarios mediante correo electrónico, garantizando la seguridad de las cuentas. Para el almacenamiento de datos, optamos por Cloud Firestore, una base de datos NoSQL que nos permite sincronizar los eventos y votos en tiempo real entre todos los dispositivos conectados.

Finalmente, para optimizar el rendimiento de la aplicación, hemos implementado programación asíncrona mediante Coroutines y Flow, lo que evita que la aplicación se congele durante las cargas de datos. La gestión de imágenes remotas se realiza a través de la librería Coil, que descarga y cachea las fotografías de los eventos de manera eficiente.


### 3. Arquitectura de la Aplicación
Hemos estructurado todo el proyecto siguiendo el patrón de arquitectura MVVM (Model - View - ViewModel). Esta decisión nos permite desacoplar la lógica de la interfaz, facilitando el mantenimiento y la escalabilidad del proyecto.

Estructura y Flujo de Datos
El proyecto se organiza en paquetes lógicos dentro de com.kaktus.app para mantener el orden. El flujo de la información dentro de la aplicación sigue un ciclo unidireccional muy claro:

En primer lugar, tenemos la capa del Modelo (Model), representada principalmente por nuestra clase de datos Event. Aquí definimos la estructura pura de la información sin preocuparnos de cómo se muestra: título, fecha, descripción, categoría, URL de la imagen y el ID del usuario creador.

En el extremo opuesto se encuentra la Vista (View), compuesta por nuestras pantallas en Compose (como HomeScreen o EventDetailScreen). Siguiendo las buenas prácticas, nuestras Vistas son "pasivas": no toman decisiones lógicas ni se conectan directamente a la base de datos. Su única función es "dibujar" en pantalla el estado actual que reciben y capturar las interacciones del usuario, como hacer clic en un botón.

El intermediario crucial es el ViewModel, concretamente nuestra clase KaktusViewModel. Este componente actúa como el cerebro de la aplicación. Es el encargado de comunicarse con Firebase para descargar o subir datos. El ViewModel expone la información a la Vista utilizando StateFlow. Gracias a este sistema reactivo, la interfaz de usuario se actualiza automáticamente: si el ViewModel detecta un cambio en la base de datos (por ejemplo, alguien añade un voto), actualiza el estado y la Vista se redibuja instantáneamente para reflejar el cambio, ofreciendo esa experiencia de "tiempo real" que buscábamos.




