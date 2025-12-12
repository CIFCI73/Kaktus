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
    // Controlliamo se c'è già un utente loggato
    val auth = FirebaseAuth.getInstance()
    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    if (isUserLoggedIn) {
        // --- UTENTE LOGGATO: MOSTRA LA HOME ---

        // 1. Inizializziamo il ViewModel
        val viewModel: KaktusViewModel = viewModel()

        // 2. Mostriamo la HomeScreen (passando i due parametri richiesti)
        HomeScreen(
            viewModel = viewModel,
            onAddEventClick = {
                // Questo codice viene eseguito quando clicchi il "+"
                // (Per ora mostra solo un messaggio)
                // Nota: In Compose dentro una funzione lambda non puoi usare "this",
                // quindi non serve passare il context al Toast in modo complesso,
                // ma per semplicità qui non mettiamo il Toast per evitare errori di Context.
                // Lo metteremo nella prossima fase.
            }
        )

    } else {
        // --- UTENTE NON LOGGATO: MOSTRA IL LOGIN ---
        LoginScreen(
            onLoginSuccess = {
                isUserLoggedIn = true // Questo fa cambiare schermata
            }
        )
    }
}