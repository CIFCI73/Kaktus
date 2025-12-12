package com.kaktus.app

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.kaktus.app.ui.theme.KaktusBeige
import com.kaktus.app.ui.theme.KaktusGreen
import com.kaktus.app.ui.theme.KaktusLightBeige

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // Variabili di stato (memoria della schermata)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) } // Se true, siamo in modalità "Registrazione"
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Layout principale: Colonna centrata
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KaktusBeige), // Il nostro sfondo sabbia
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titolo
            Text(
                text = "Kaktus",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = KaktusGreen
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KaktusGreen,
                    focusedLabelColor = KaktusGreen,
                    cursorColor = KaktusGreen
                ),
                modifier = Modifier.fillMaxWidth().background(KaktusLightBeige)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Nasconde la password con i pallini
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KaktusGreen,
                    focusedLabelColor = KaktusGreen,
                    cursorColor = KaktusGreen
                ),
                modifier = Modifier.fillMaxWidth().background(KaktusLightBeige)
            )

            // Messaggio di errore (se presente)
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottone Login / Registrati
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        if (isRegistering) {
                            // LOGICA REGISTRAZIONE
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess() // Vai alla Home
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Errore sconosciuto"
                                    }
                                }
                        } else {
                            // LOGICA LOGIN
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess() // Vai alla Home
                                    } else {
                                        errorMessage = "Login fallito. Controlla email e password."
                                    }
                                }
                        }
                    } else {
                        isLoading = false
                        errorMessage = "Riempi tutti i campi!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KaktusGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = if (isRegistering) "Registrati" else "Accedi", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Testo per cambiare modalità
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    text = if (isRegistering) "Hai già un account? Accedi" else "Non hai un account? Registrati",
                    color = KaktusGreen
                )
            }
        }
    }
}