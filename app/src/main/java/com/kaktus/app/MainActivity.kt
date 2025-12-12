package com.kaktus.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kaktus.app.ui.theme.KaktusTheme // Assicurati che questo import sia corretto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Se ti dà errore su questo, cancellalo pure
        setContent {
            KaktusTheme { // Usa il tema creato da Android Studio

                // Qui chiamiamo la nostra schermata
                LoginScreen(
                    onLoginSuccess = {
                        // Cosa succede quando il login va a buon fine?
                        // Per ora mostriamo solo un messaggio temporaneo
                        Toast.makeText(this, "Benvenuto su Kaktus!", Toast.LENGTH_SHORT).show()

                        // PROSSIMO STEP: Qui navigheremo alla Home
                    }
                )
            }
        }
    }
}
