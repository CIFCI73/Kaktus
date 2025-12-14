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


### 4. Diseño de la Base de Datos (Cloud Firestore)
Para la persistencia de los datos, hemos optado por Cloud Firestore, la base de datos NoSQL de nueva generación de Google. Esta elección se fundamenta en la flexibilidad que ofrecen los esquemas libres de documentos frente a las tablas rígidas de SQL, lo cual es ideal para una aplicación en constante evolución como Kaktus. Además, la capacidad nativa de Firestore para sincronizar datos en tiempo real fue determinante: queríamos que si un usuario votaba por un evento, el contador se actualizase instantáneamente en las pantallas de todos los demás usuarios sin necesidad de recargar la página.

La estructura de la base de datos es sencilla pero eficiente. Trabajamos principalmente con una colección raíz denominada events. Dentro de esta colección, cada documento representa un evento único y contiene toda la información necesaria para su visualización y gestión.

A nivel de esquema, cada documento de evento almacena campos de texto estándar como el title (título), description (descripción detallada), location (ubicación) y date (fecha). Para la gestión de medios y enlaces externos, almacenamos las URLs como cadenas de texto (imageUrl, mapsLink, ticketLink), delegando la carga del contenido a la aplicación cliente. Un aspecto crucial del diseño es el campo userId. Este campo almacena el identificador único (UID) del usuario que creó el evento. Gracias a este metadato, podemos implementar lógica de seguridad y privacidad, permitiendo que en la pantalla de perfil cada usuario solo pueda gestionar y eliminar los eventos que le pertenecen, protegiendo así la integridad de los datos de la comunidad.

Por último, el campo votes es un entero numérico que actúa como contador de popularidad. La aplicación escucha activamente los cambios en este campo para reordenar la lista de eventos en la pantalla principal, mostrando siempre los más populares en la parte superior.

### 5. Lógica de Navegación y Flujo de la Aplicación
En lugar de utilizar el sistema tradicional de navegación basado en Fragmentos o múltiples Actividades, hemos implementado una navegación moderna basada en estados dentro de una única actividad (MainActivity). Este enfoque, conocido como "Single Activity Architecture", simplifica enormemente el ciclo de vida de la aplicación.

La MainActivity actúa como un orquestador. Define variables de estado observables (usando remember y mutableStateOf) que determinan qué pantalla debe mostrarse en cada momento. Por ejemplo, la variable isUserLoggedIn determina si el usuario debe ver la pantalla de Login o la Home. Del mismo modo, variables como selectedEvent o isAddingEvent actúan como interruptores: si selectedEvent contiene un evento, la aplicación "dibuja" la pantalla de detalles sobre la actual; si es nulo, vuelve a mostrar la lista general. Este sistema condicional hace que la navegación sea fluida y muy rápida, ya que no estamos destruyendo y creando actividades pesadas continuamente.

### 6. Análisis Detallado de las Funcionalidades
La aplicación se divide en varios módulos funcionales que interactúan entre sí para ofrecer una experiencia completa. A continuación, se detalla la lógica detrás de cada uno.

#### 6.1 Autenticación y Seguridad
El punto de entrada a la aplicación es la pantalla de Login. Para evitar la complejidad y los riesgos de seguridad que conlleva gestionar contraseñas y encriptación manualmente, hemos delegado esta responsabilidad en Firebase Authentication.

Cuando el usuario introduce su correo y contraseña, la aplicación realiza una petición asíncrona a los servidores de Google. Si las credenciales son correctas, el sistema devuelve un token de sesión y el objeto currentUser. Hemos implementado una lógica de persistencia automática: al iniciar la aplicación, verificamos si ya existe una sesión activa. Si es así, el usuario es redirigido directamente a la pantalla principal, saltándose el login y mejorando la experiencia de uso.

#### 6.2 Descubrimiento y Filtrado (Pantalla Principal)
La HomeScreen es el corazón de Kaktus. Aquí, el desafío técnico principal fue gestionar la visualización eficiente de listas que podrían contener cientos de eventos. Para ello utilizamos el componente LazyColumn de Jetpack Compose, que renderiza de manera inteligente solo los elementos que son visibles en la pantalla del móvil, reciclando los recursos a medida que el usuario hace scroll.

Para mejorar la usabilidad, implementamos un sistema de filtrado dual y reactivo. En la parte superior, una barra de búsqueda permite al usuario escribir texto. Simultáneamente, una fila de "chips" (etiquetas) permite seleccionar categorías (Música, Deporte, etc.). La lógica de filtrado combina ambas entradas en tiempo real: la lista solo muestra los eventos que coinciden con la categoría seleccionada Y que contienen el texto buscado en su título o ubicación. Esta operación se realiza en el cliente, lo que garantiza una respuesta inmediata mientras el usuario escribe.

#### 6.3 Creación y Gestión de Contenido
La pantalla AddEventScreen permite a los usuarios contribuir al ecosistema. Hemos diseñado un formulario que incluye validaciones básicas para asegurar que no se envíen eventos vacíos. Un punto destacado de esta pantalla es la integración de un selector de fecha nativo (DatePickerDialog), que mejora la experiencia de usuario evitando errores de formato manual al escribir fechas.

Al pulsar el botón de guardar, el ViewModel recopila todos los datos, adjunta automáticamente el ID del usuario actual (recuperado de la sesión de Auth) y envía el objeto a Firestore. Una vez confirmada la escritura en la base de datos, la aplicación navega automáticamente de vuelta a la pantalla anterior y la lista se actualiza sola gracias a la suscripción en tiempo real.

#### 6.4 Vista de Detalle y Experiencia Inmersiva
Cuando un usuario selecciona un evento, se abre la EventDetailScreen. A diferencia de las tarjetas resumen de la pantalla principal, esta vista está diseñada para ser inmersiva. Muestra la imagen de cabecera a gran tamaño y despliega la descripción completa del evento.

Desde el punto de vista de la interactividad, esta pantalla conecta la aplicación con el mundo exterior mediante el uso de "Intents" de Android. Al hacer clic en los botones de "Mapa" o "Entradas", la aplicación lanza una intención para abrir la aplicación de Google Maps o el navegador web respectivamente, dirigiendo al usuario a la ubicación exacta o a la venta de tickets.



