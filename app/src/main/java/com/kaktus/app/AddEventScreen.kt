package com.kaktus.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.* // <--- QUESTA È FONDAMENTALE PER RISOLVERE I PROBLEMI DI "getValue"
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
// Import specifici per evitare l'errore "delegate getValue"
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: KaktusViewModel,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var mapsLink by remember { mutableStateOf("") }
    var ticketLink by remember { mutableStateOf("") }

    val categories = listOf("Música", "Deporte", "Comida", "Arte", "Noche", "Otro")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Evento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Atrás") }
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
            Text("Detalles del Evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = KaktusGreen)

            KaktusTextField(value = title, onValueChange = { title = it }, label = "Título")

            // CAMPO DESCRIPCIÓN
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(KaktusLightBeige),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KaktusGreen, focusedLabelColor = KaktusGreen),
                maxLines = 5
            )

            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Fecha") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = KaktusGreen)
                    }
                },
                modifier = Modifier.fillMaxWidth().background(KaktusLightBeige),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KaktusGreen, focusedLabelColor = KaktusGreen)
            )

            KaktusTextField(value = location, onValueChange = { location = it }, label = "Lugar")

            Text("Categoría", fontWeight = FontWeight.Bold, color = KaktusGreen)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = (category == selectedCategory),
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = KaktusGreen, selectedLabelColor = Color.White)
                    )
                }
            }

            Divider(color = KaktusGreen, thickness = 1.dp)

            KaktusTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "Enlace de Imagen (URL)")
            KaktusTextField(value = mapsLink, onValueChange = { mapsLink = it }, label = "Enlace Google Maps")
            KaktusTextField(value = ticketLink, onValueChange = { ticketLink = it }, label = "Enlace Entradas")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && date.isNotEmpty()) {
                        viewModel.saveEvent(
                            title, date, location, description, selectedCategory,
                            imageUrl, mapsLink, ticketLink,
                            onSuccess = { onBackClick() }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text("Publicar", fontSize = 18.sp)
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { date = dateFormatter.format(Date(it)) }
                        showDatePicker = false
                    }) { Text("Aceptar", color = KaktusGreen) }
                }
            ) { DatePicker(state = datePickerState) }
        }
    }
}

@Composable
fun KaktusTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().background(KaktusLightBeige),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KaktusGreen, focusedLabelColor = KaktusGreen)
    )
}