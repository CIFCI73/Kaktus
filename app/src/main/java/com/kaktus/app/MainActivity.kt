package com.kaktus.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.kaktus.app.ui.theme.KaktusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaktusTheme {
                KaktusApp()
            }
        }
    }
}

@Composable
fun KaktusApp() {
    val auth = FirebaseAuth.getInstance()
    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    // ESTADOS DE NAVEGACIÓN
    var isAddingEvent by remember { mutableStateOf(false) }
    var isProfileOpen by remember { mutableStateOf(false) } // <--- NUEVO ESTADO
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    if (isUserLoggedIn) {
        val viewModel: KaktusViewModel = viewModel()

        if (isAddingEvent) {
            AddEventScreen(
                viewModel = viewModel,
                onBackClick = { isAddingEvent = false }
            )
        } else if (isProfileOpen) {
            // --- MOSTRAR PERFIL ---
            ProfileScreen(
                viewModel = viewModel,
                onBackClick = { isProfileOpen = false },
                onLogoutClick = {
                    auth.signOut()
                    isUserLoggedIn = false
                    isProfileOpen = false
                },
                onEventClick = { event ->
                    selectedEvent = event // Abre los detalles también desde el perfil
                }
            )
        } else if (selectedEvent != null) {
            EventDetailScreen(
                event = selectedEvent!!,
                onBackClick = { selectedEvent = null },
                onVoteClick = { viewModel.onVoteClick(selectedEvent!!) }
            )
        } else {
            // --- MOSTRAR INICIO ---
            HomeScreen(
                viewModel = viewModel,
                onAddEventClick = { isAddingEvent = true },
                onLogoutClick = { isProfileOpen = true }, // <--- AHORA ABRE EL PERFIL
                onEventClick = { event -> selectedEvent = event }
            )
        }

    } else {
        LoginScreen(onLoginSuccess = { isUserLoggedIn = true })
    }
}