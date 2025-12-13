package com.kaktus.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: KaktusViewModel,
    onBackClick: () -> Unit
) {
    // Variabili per i campi di testo
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var mapsLink by remember { mutableStateOf("") }
    var ticketLink by remember { mutableStateOf("") }

    // Variabili per il CALENDARIO
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val isLoading by viewModel.isLoading.collectAsState()

    // Logica per formattare la data (da millisecondi a testo "gg/mm/aaaa")
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Dettagli Evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = KaktusGreen)

            // Titolo
            KaktusTextField(value = title, onValueChange = { title = it }, label = "Titolo (es. Jazz Festival)")

            // --- CAMPO DATA CON CALENDARIO ---
            OutlinedTextField(
                value = date,
                onValueChange = { }, // Non facciamo nulla se l'utente prova a scrivere
                label = { Text("Data") },
                readOnly = true, // L'utente non può digitare a mano
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Scegli Data", tint = KaktusGreen)
                    }
                },
                modifier = Modifier.fillMaxWidth().background(KaktusLightBeige),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KaktusGreen,
                    focusedLabelColor = KaktusGreen,
                    cursorColor = KaktusGreen
                )
            )

            // Luogo
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
                            onSuccess = { onBackClick() }
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

        // --- IL COMPONENTE DIALOGO CALENDARIO ---
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                date = dateFormatter.format(Date(millis))
                            }
                            showDatePicker = false
                        }
                    ) { Text("OK", color = KaktusGreen) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Annulla", color = KaktusGreen) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

// Componente per i campi di testo normali
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