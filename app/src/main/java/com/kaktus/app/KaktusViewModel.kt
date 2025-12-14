package com.kaktus.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KaktusViewModel : ViewModel() {

    // Il database
    private val db = FirebaseFirestore.getInstance()

    // La lista degli eventi (inizialmente vuota) che la UI osserverà
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events
    private val _userEvents = MutableStateFlow<List<Event>>(emptyList())
    val userEvents: StateFlow<List<Event>> = _userEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Appena l'app parte, inizia ad ascoltare il database
        fetchEvents()
    }

    fun fetchUserEvents() {
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            db.collection("events")
                .whereEqualTo("userId", currentUserId) // <--- FILTRO MAGICO
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

            // Ascoltiamo la collezione "events" ordinata per voti (dal più alto al più basso)
            db.collection("events")
                .orderBy("votes", com.google.firebase.firestore.Query.Direction.DESCENDING) // <--- AGGIUNGI QUESTA RIGA
                .addSnapshotListener { snapshot, e ->
                    // ... il resto rimane uguale
                    if (e != null) {
                        println("Errore nel caricamento eventi: ${e.message}")
                        _isLoading.value = false
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        // Trasformiamo i documenti Firebase in oggetti Event
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

        // Recuperiamo l'ID dell'utente loggato
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
            "userId" to currentUserId // <--- SALVIAMO L'ID
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

    // Funzione per aggiungere un voto
    fun onVoteClick(event: Event) {
        // Aggiorniamo il documento su Firebase
        // FieldValue.increment(1) è sicuro: se 100 persone cliccano insieme, li conta tutti
        db.collection("events").document(event.id)
            .update("votes", com.google.firebase.firestore.FieldValue.increment(1))
            .addOnFailureListener { e ->
                println("Errore voto: $e")
            }
    }


    // Funzione per eliminare un evento
    fun deleteEvent(event: Event) {
        db.collection("events").document(event.id)
            .delete()
            .addOnSuccessListener {
                println("Evento eliminato!")
            }
            .addOnFailureListener { e ->
                println("Errore eliminazione: $e")
            }
    }

    // Funzione per aggiungere un voto (la useremo dopo)
    fun voteEvent(eventId: String) {
        // Incrementa il voto di 1
        // Nota: questa è una operazione "atomica" su Firebase
        // Per ora lasciamola vuota, la riempiremo quando facciamo la UI dei voti
    }
}