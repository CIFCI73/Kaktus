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
    }

    fun fetchUserEvents() {
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            db.collection("events")
                .whereEqualTo("userId", currentUserId) // <--- FILTRO MÁGICO
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

            // Escuchamos la colección "events" ordenada por votos (del más alto al más bajo)
            db.collection("events")
                .orderBy("votes", com.google.firebase.firestore.Query.Direction.DESCENDING) // <--- AÑADIR ESTA LÍNEA
                .addSnapshotListener { snapshot, e ->
                    // ... el resto permanece igual
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

    // Función para añadir un voto (la usaremos después)
    fun voteEvent(eventId: String) {
        // Incrementa el voto en 1
        // Nota: esta es una operación "atómica" en Firebase
        // Por ahora la dejamos vacía, la llenaremos cuando hagamos la UI de votos
    }
}