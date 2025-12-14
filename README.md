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




