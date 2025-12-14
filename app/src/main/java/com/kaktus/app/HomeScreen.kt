package com.kaktus.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
    onLogoutClick: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- ESTADO DEL FILTRO (Traducido) ---
    val categories = listOf("Todos", "Música", "Deporte", "Comida", "Arte", "Noche", "Otro")
    var selectedCategory by remember { mutableStateOf("Todos") }

    // --- ESTADO DE BÚSQUEDA ---
    var searchQuery by remember { mutableStateOf("") }

    // --- LÓGICA DE FILTRADO COMBINADA ---
    val filteredEvents = events.filter { event ->
        // Nota: Asegúrate de que las categorías guardadas en Firebase coincidan con las traducidas
        // O implementa una lógica de mapeo si "Music" está guardado como "Musica" en la DB
        val matchCategory = (selectedCategory == "Todos" || event.category == selectedCategory)

        val matchSearch = if (searchQuery.isEmpty()) {
            true
        } else {
            event.title.contains(searchQuery, ignoreCase = true) ||
                    event.location.contains(searchQuery, ignoreCase = true)
        }

        matchCategory && matchSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos Gran Canaria", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KaktusBeige,
                    titleContentColor = KaktusGreen
                ),
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = KaktusGreen)
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
                Icon(Icons.Default.Add, contentDescription = "Añadir Evento")
            }
        },
        containerColor = KaktusBeige
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {

            // --- 1. BARRA DE BÚSQUEDA ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar título o lugar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KaktusGreen) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(KaktusLightBeige, RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KaktusGreen,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = KaktusGreen
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // --- 2. FILTROS CATEGORÍA ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = (category == selectedCategory),
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KaktusGreen,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = KaktusGreen,
                            enabled = true,
                            selected = (category == selectedCategory)
                        )
                    )
                }
            }

            // --- 3. LISTA DE EVENTOS ---
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KaktusGreen)
                }
            } else {
                if (filteredEvents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron eventos 🌵", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredEvents) { event ->
                            EventCard(
                                event = event,
                                onVoteClick = { viewModel.onVoteClick(event) },
                                onClick = { onEventClick(event) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onVoteClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = KaktusLightBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Box {
            Column {
                if (event.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = "Foto del evento",
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(150.dp).background(KaktusGreen.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin Imagen", color = KaktusGreen)
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.category.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = KaktusGreen.copy(alpha = 0.7f)
                    )
                    Text(text = event.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = KaktusGreen)
                    Text(text = event.date, fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaktusGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = event.location, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

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
                        ) { Text("Mapa") }

                        Button(
                            onClick = {
                                if (event.ticketLink.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.ticketLink))
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) { Text("Entradas") }
                    }
                }
            }

            // Voto
            Surface(
                color = KaktusLightBeige.copy(alpha = 0.9f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).clickable { onVoteClick() }
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${event.votes}", fontWeight = FontWeight.Bold, color = KaktusGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Votar", tint = Color.Red, modifier = Modifier.size(20.dp))
                }
            }

            if (onDeleteClick != null) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(Color.White.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}