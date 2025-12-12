package com.kaktus.app

// Questa classe rappresenta un singolo evento
data class Event(
    val id: String = "",             // ID univoco di Firebase
    val title: String = "",          // Es. "Jazz Festival"
    val date: String = "",           // Es. "12/08/2025"
    val location: String = "",       // Es. "Maspalomas"
    val description: String = "",    // Info aggiuntive
    val imageUrl: String = "",       // Link all'immagine (se c'è)
    val mapsLink: String = "",       // Link Google Maps
    val ticketLink: String = "",     // Link biglietti
    val votes: Int = 0               // Numero di voti
)