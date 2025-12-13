package com.kaktus.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onAddEventClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventi Gran Canaria", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KaktusBeige,
                    titleContentColor = KaktusGreen
                ),
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = KaktusGreen)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEventClick,
                containerColor = KaktusGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Evento")
            }
        },
        containerColor = KaktusBeige
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KaktusGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onVoteClick = { viewModel.onVoteClick(event) },
                        onDeleteClick = { viewModel.deleteEvent(event) } // <--- NUOVO: CANCELLA
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onVoteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = KaktusLightBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Column {
                // 1. Immagine
                if (event.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = "Foto evento",
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(150.dp).background(KaktusGreen.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nessuna Immagine", color = KaktusGreen)
                    }
                }

                // 2. Dati
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = event.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = KaktusGreen)
                    Text(text = event.date, fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaktusGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = event.location, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Bottoni Link
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (event.mapsLink.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.mapsLink))
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) { Text("Mappa") }

                        Button(
                            onClick = {
                                if (event.ticketLink.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.ticketLink))
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) { Text("Biglietti") }
                    }
                }
            }

            // --- CUORE PER VOTARE (In alto a DESTRA) ---
            Surface(
                color = KaktusLightBeige.copy(alpha = 0.9f),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable { onVoteClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${event.votes}", fontWeight = FontWeight.Bold, color = KaktusGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Vota", tint = Color.Red, modifier = Modifier.size(20.dp))
                }
            }

            // --- CESTINO PER CANCELLARE (In alto a SINISTRA) ---
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopStart) // <--- Posizione sinistra
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape) // Sfondo semitrasparente per vederlo bene
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Elimina",
                    tint = Color.Red
                )
            }
        }
    }
}