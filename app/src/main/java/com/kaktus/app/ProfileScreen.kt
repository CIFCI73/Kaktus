package com.kaktus.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.kaktus.app.ui.theme.KaktusBeige
import com.kaktus.app.ui.theme.KaktusGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: KaktusViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    // Carichiamo gli eventi dell'utente appena apriamo la pagina
    LaunchedEffect(Unit) {
        viewModel.fetchUserEvents()
    }

    val userEvents by viewModel.userEvents.collectAsState()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Utente"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Il mio Profilo") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KaktusBeige)
            )
        },
        containerColor = KaktusBeige
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // --- INTESTAZIONE PROFILO ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icona Profilo Grande
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(KaktusGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = userEmail, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KaktusGreen)

                Spacer(modifier = Modifier.height(16.dp))

                // Tasto Logout
                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Esci")
                }
            }

            Divider(color = KaktusGreen, thickness = 1.dp)

            // --- LISTA "I MIEI EVENTI" ---
            Text(
                text = "Eventi creati da me (${userEvents.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = KaktusGreen,
                modifier = Modifier.padding(16.dp)
            )

            if (userEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Non hai ancora creato eventi.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(userEvents) { event ->
                        EventCard(
                            event = event,
                            onVoteClick = { viewModel.onVoteClick(event) },
                            onDeleteClick = { viewModel.deleteEvent(event) },
                            onClick = { onEventClick(event) }
                        )
                    }
                }
            }
        }
    }
}