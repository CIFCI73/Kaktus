package com.kaktus.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onBackClick: () -> Unit,
    onVoteClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = KaktusGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = KaktusBeige
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. IMAGEN GRANDE
            if (event.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp).background(KaktusGreen.copy(alpha = 0.3f)))
            }

            // 2. CONTENIDO
            Column(modifier = Modifier.padding(24.dp)) {

                // Categoría y Votos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(event.category) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = KaktusGreen)
                    )

                    // Botón Voto
                    Button(
                        onClick = onVoteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${event.votes}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(event.title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = KaktusGreen, lineHeight = 36.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha y Lugar
                Text(event.date, fontSize = 18.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaktusGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.location, fontSize = 18.sp, color = KaktusGreen)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = KaktusGreen.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(24.dp))

                // DESCRIPCIÓN COMPLETA
                Text("Descripción", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KaktusGreen)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (event.description.isNotEmpty()) event.description else "No hay descripción disponible.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // BOTONES MAPA Y ENTRADAS
                Button(
                    onClick = {
                        if (event.mapsLink.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.mapsLink))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen)
                ) { Text("Ver en Mapa", fontSize = 18.sp) }

                Spacer(modifier = Modifier.height(12.dp))

                if (event.ticketLink.isNotEmpty()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.ticketLink))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) { Text("Comprar Entradas", fontSize = 18.sp) }
                }
            }
        }
    }
}