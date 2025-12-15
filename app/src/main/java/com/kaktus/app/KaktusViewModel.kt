package com.kaktus.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KaktusViewModel : ViewModel() {

    // La base de datos
    private val db = FirebaseFirestore.getInstance()

    // La lista de eventos (inicialmente vacía) que la UI observará
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events
    private val _userEvents = MutableStateFlow<List<Event>>(emptyList())
    val userEvents: StateFlow<List<Event>> = _userEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Apenas inicia la app, comienza a escuchar la base de datos
        fetchEvents()

        //populateSecondUserEvents()
        //populateDatabase()
    }

    fun fetchUserEvents() {
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            db.collection("events")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        val myEvents = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Event::class.java)?.copy(id = doc.id)
                        }
                        _userEvents.value = myEvents
                        _isLoading.value = false
                    }
                }
        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true

            db.collection("events")
                .orderBy("votes", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Error al cargar eventos: ${e.message}")
                        _isLoading.value = false
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        // Transformamos los documentos de Firebase en objetos Event
                        val eventsList = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Event::class.java)?.copy(id = doc.id)
                        }
                        _events.value = eventsList
                        _isLoading.value = false
                    }
                }
        }
    }

    fun saveEvent(
        title: String,
        date: String,
        location: String,
        description: String,
        category: String,
        imageUrl: String,
        mapsLink: String,
        ticketLink: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true

        // Recuperamos el ID del usuario logueado
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val newEvent = hashMapOf(
            "title" to title,
            "date" to date,
            "location" to location,
            "description" to description,
            "category" to category,
            "imageUrl" to imageUrl,
            "mapsLink" to mapsLink,
            "ticketLink" to ticketLink,
            "votes" to 0,
            "userId" to currentUserId // <--- GUARDAMOS EL ID
        )

        db.collection("events").add(newEvent)
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    // Función temporal para poblar la base de datos con datos de prueba
    fun populateDatabase() {
        // Usamos el ID del usuario actual para que puedas editarlos/borrarlos desde tu perfil
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "seed_user"

        val sampleEvents = listOf(
            hashMapOf(
                "title" to "Noche de Tapas en Vegueta",
                "date" to "14/12/2025",
                "location" to "Vegueta, Las Palmas",
                "description" to "Disfruta de la tradicional ruta de los pinchos por el casco antiguo de la ciudad cada jueves. Gastronomía local, vinos de la tierra y buen ambiente en las calles empedradas.",
                "category" to "Comida",
                "imageUrl" to "https://images.unsplash.com/photo-1515443961218-a51367888e4b?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/vegueta",
                "ticketLink" to "",
                "votes" to 45,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Gran Canaria Surf Open",
                "date" to "20/01/2026",
                "location" to "La Cícer, Las Canteras",
                "description" to "Campeonato regional de surf. Ven a ver a los mejores riders cabalgar las olas de La Cícer en un ambiente deportivo y playero único.",
                "category" to "Deporte",
                "imageUrl" to "https://images.unsplash.com/photo-1502680390469-be75c86b636f?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/lacicer",
                "ticketLink" to "",
                "votes" to 89,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Concierto de Jazz al Atardecer",
                "date" to "05/02/2026",
                "location" to "Muelle Deportivo",
                "description" to "Música en vivo con vistas al mar. Un evento relajado para disfrutar con amigos mientras cae el sol sobre los barcos.",
                "category" to "Música",
                "imageUrl" to "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/muelle",
                "ticketLink" to "https://entradas.com",
                "votes" to 120,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Exposición: Arte y Volcán",
                "date" to "10/12/2025",
                "location" to "CAAM - Centro Atlántico de Arte Moderno",
                "description" to "Una retrospectiva fascinante sobre la influencia del paisaje volcánico en el arte contemporáneo canario. Entrada gratuita.",
                "category" to "Arte",
                "imageUrl" to "https://images.unsplash.com/photo-1518998053901-5348d3969104?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/caam",
                "ticketLink" to "",
                "votes" to 34,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Senderismo al Roque Nublo",
                "date" to "12/03/2026",
                "location" to "Tejeda, Gran Canaria",
                "description" to "Ruta guiada hasta el emblemático Roque Nublo. Incluye transporte desde la capital y picnic con productos locales.",
                "category" to "Otro",
                "imageUrl" to "https://images.unsplash.com/photo-1582487920023-e5d443204948?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/roquenublo",
                "ticketLink" to "https://tours.com",
                "votes" to 210,
                "userId" to currentUserId
            )
        )

        sampleEvents.forEach { event ->
            db.collection("events").add(event)
                .addOnSuccessListener { println("Evento '${event["title"]}' añadido correctamente.") }
                .addOnFailureListener { e -> println("Error añadiendo evento: $e") }
        }
    }

    // Función para poblar eventos de un SEGUNDO usuario
    fun populateSecondUserEvents() {

        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "seed_user"

        val secondBatchEvents = listOf(
            hashMapOf(
                "title" to "Gala Drag Queen 2026",
                "date" to "25/02/2026",
                "location" to "Parque Santa Catalina",
                "description" to "¡El evento más icónico del año! Plataformas infinitas, purpurina y el mejor espectáculo del Carnaval de Las Palmas. Aforo limitado.",
                "category" to "Fiesta",
                "imageUrl" to "https://images.unsplash.com/photo-1533174072545-e8d4aa97edf9?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/santacatalina",
                "ticketLink" to "https://lpacarnaval.com",
                "votes" to 340,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Ruta del Almendro en Flor",
                "date" to "02/02/2026",
                "location" to "Tejeda",
                "description" to "Caminata fotográfica para ver florecer los almendros. Un espectáculo natural blanco y rosa en las cumbres de la isla.",
                "category" to "Naturaleza",
                "imageUrl" to "https://images.unsplash.com/photo-1523712999610-f77fbcfc3843?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/tejeda",
                "ticketLink" to "",
                "votes" to 65,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Nomad City: Tech Meetup",
                "date" to "15/01/2026",
                "location" to "Auditorio Alfredo Kraus",
                "description" to "Encuentro internacional de nómadas digitales y desarrolladores. Networking, charlas sobre IA y café de Agaete.",
                "category" to "Tecnología",
                "imageUrl" to "https://images.unsplash.com/photo-1531482615713-2afd69097998?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/auditorio",
                "ticketLink" to "https://meetup.com",
                "votes" to 112,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Buceo en la Reserva de El Cabrón",
                "date" to "10/04/2026",
                "location" to "Arinaga",
                "description" to "Inmersión guiada para todos los niveles. Descubre la rica biodiversidad marina de una de las mejores zonas de buceo de España.",
                "category" to "Deporte",
                "imageUrl" to "https://images.unsplash.com/photo-1682687220742-aba13b6e50ba?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/arinaga",
                "ticketLink" to "https://diving.com",
                "votes" to 28,
                "userId" to currentUserId
            ),
            hashMapOf(
                "title" to "Cine de Verano: Clásicos",
                "date" to "20/08/2026",
                "location" to "Plaza de la Música",
                "description" to "Proyección al aire libre de películas clásicas de los 80. Trae tu silla o toalla y disfruta de una noche de cine bajo las estrellas.",
                "category" to "Arte",
                "imageUrl" to "https://images.unsplash.com/photo-1517604931442-7105376f7c04?q=80&w=1000&auto=format&fit=crop",
                "mapsLink" to "https://goo.gl/maps/plazamusica",
                "ticketLink" to "",
                "votes" to 95,
                "userId" to currentUserId
            )
        )

        secondBatchEvents.forEach { event ->
            db.collection("events").add(event)
                .addOnSuccessListener { println("Evento Usuario 2 - '${event["title"]}' añadido.") }
                .addOnFailureListener { e -> println("Error: $e") }
        }
    }

    // Función para añadir un voto
    fun onVoteClick(event: Event) {
        // Actualizamos el documento en Firebase
        // FieldValue.increment(1) es seguro: si 100 personas hacen clic a la vez, los cuenta todos
        db.collection("events").document(event.id)
            .update("votes", com.google.firebase.firestore.FieldValue.increment(1))
            .addOnFailureListener { e ->
                println("Error voto: $e")
            }
    }


    // Función para eliminar un evento
    fun deleteEvent(event: Event) {
        db.collection("events").document(event.id)
            .delete()
            .addOnSuccessListener {
                println("¡Evento eliminado!")
            }
            .addOnFailureListener { e ->
                println("Error eliminación: $e")
            }
    }


    fun voteEvent(eventId: String) {

    }
}