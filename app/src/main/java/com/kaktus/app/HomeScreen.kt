package com.kaktus.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kaktus.app.ui.theme.KaktusBeige
import com.kaktus.app.ui.theme.KaktusGreen
import com.kaktus.app.ui.theme.KaktusLightBeige

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: KaktusViewModel,
    onAddEventClick: () -> Unit // Funzione che chiameremo quando si clicca il "+"
) {
    // Osserviamo la lista degli eventi dal ViewModel
    // "collectAsState" fa sì che la UI si aggiorni da sola quando il database cambia
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        // Barra superiore (TopBar)
        topBar = {
            TopAppBar(
                title = { Text("Eventi Gran Canaria", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KaktusBeige,
                    titleContentColor = KaktusGreen
                )
            )
        },
        // Bottone galleggiante (+)
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEventClick,
                containerColor = KaktusGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Evento")
            }
        },
        containerColor = KaktusBeige // Sfondo generale sabbia
    ) { paddingValues ->

        // Contenuto principale
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KaktusGreen)
            }
        } else {
            // Lista scorrevole
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Spazio tra le card
            ) {
                items(events) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = KaktusLightBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // 1. Immagine dell'evento (Caricata da internet con Coil)
            if (event.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Foto evento",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder se non c'è immagine (un box verde)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(KaktusGreen.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessuna Immagine", color = KaktusGreen)
                }
            }

            // 2. Dati dell'evento
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KaktusGreen
                )
                Text(
                    text = event.date,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Riga Luogo con icona
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaktusGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = event.location, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Bottoni Azione (Mappa e Biglietti)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Bottone MAPPA (Apre Google Maps)
                    Button(
                        onClick = {
                            if (event.mapsLink.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.mapsLink))
                                context.startActivity(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen),
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Text("Mappa")
                    }

                    // Bottone BIGLIETTI (Apre il browser)
                    Button(
                        onClick = {
                            if (event.ticketLink.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.ticketLink))
                                context.startActivity(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black), // Nero per contrasto
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Text("Biglietti")
                    }
                }
            }
        }
    }
}