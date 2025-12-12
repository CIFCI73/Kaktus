package com.kaktus.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaktus.app.ui.theme.KaktusBeige
import com.kaktus.app.ui.theme.KaktusGreen
import com.kaktus.app.ui.theme.KaktusLightBeige

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: KaktusViewModel,
    onBackClick: () -> Unit // Per tornare indietro senza salvare
) {
    // Variabili per i campi di testo
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") } // L'utente incollerà un link URL per ora
    var mapsLink by remember { mutableStateOf("") }
    var ticketLink by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuovo Evento") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permette di scorrere se la tastiera copre
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Dettagli Evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = KaktusGreen)

            // CAMPI DI TESTO
            KaktusTextField(value = title, onValueChange = { title = it }, label = "Titolo (es. Jazz Festival)")
            KaktusTextField(value = date, onValueChange = { date = it }, label = "Data (es. 12/08/2025)")
            KaktusTextField(value = location, onValueChange = { location = it }, label = "Luogo (es. Maspalomas)")

            Divider(color = KaktusGreen, thickness = 1.dp)
            Text("Link Utili", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = KaktusGreen)

            KaktusTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "Link Immagine (URL)")
            KaktusTextField(value = mapsLink, onValueChange = { mapsLink = it }, label = "Link Google Maps")
            KaktusTextField(value = ticketLink, onValueChange = { ticketLink = it }, label = "Link Biglietti")

            Spacer(modifier = Modifier.height(16.dp))

            // BOTTONE SALVA
            Button(
                onClick = {
                    if (title.isNotEmpty() && date.isNotEmpty()) {
                        viewModel.saveEvent(
                            title, date, location, imageUrl, mapsLink, ticketLink,
                            onSuccess = { onBackClick() } // Quando salva, torna alla Home
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Pubblica Evento", fontSize = 18.sp)
                }
            }
        }
    }
}

// Un piccolo componente per non riscrivere lo stile ogni volta
@Composable
fun KaktusTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().background(KaktusLightBeige),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KaktusGreen,
            focusedLabelColor = KaktusGreen,
            cursorColor = KaktusGreen
        )
    )
}