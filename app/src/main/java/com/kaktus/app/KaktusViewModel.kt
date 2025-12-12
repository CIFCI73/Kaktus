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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Appena l'app parte, inizia ad ascoltare il database
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true

            // Ascoltiamo la collezione "events" in tempo reale
            db.collection("events")
                .addSnapshotListener { snapshot, e ->
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

    // Funzione per aggiungere un voto (la useremo dopo)
    fun voteEvent(eventId: String) {
        // Incrementa il voto di 1
        // Nota: questa è una operazione "atomica" su Firebase
        // Per ora lasciamola vuota, la riempiremo quando facciamo la UI dei voti
    }
}