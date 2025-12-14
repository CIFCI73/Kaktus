package com.kaktus.app

data class Event(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val description: String = "",
    val category: String = "Otro", // <--- CAMBIADO DE "Altro" A "Otro"
    val imageUrl: String = "",
    val mapsLink: String = "",
    val ticketLink: String = "",
    val votes: Int = 0,
    val userId: String = ""
)