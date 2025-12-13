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
    // Stato Login
    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    // Stato Navigazione: stiamo aggiungendo un evento?
    var isAddingEvent by remember { mutableStateOf(false) }

    if (isUserLoggedIn) {

        // Inizializziamo il ViewModel
        val viewModel: KaktusViewModel = viewModel()

        if (isAddingEvent) {
            // --- MOSTRA SCHERMATA AGGIUNGI ---
            AddEventScreen(
                viewModel = viewModel,
                onBackClick = { isAddingEvent = false } // Torna alla Home
            )
        } else {
            // --- MOSTRA HOME ---
            HomeScreen(
                viewModel = viewModel,
                onAddEventClick = { isAddingEvent = true },
                onLogoutClick = {
                    auth.signOut()       // Disconnette Firebase
                    isUserLoggedIn = false // Cambia schermata
                }
            )
        }

    } else {
        // --- MOSTRA LOGIN ---
        LoginScreen(
            onLoginSuccess = { isUserLoggedIn = true }
        )
    }
}