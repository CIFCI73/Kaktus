package com.kaktus.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaktus.app.ui.theme.KaktusBeige
import com.kaktus.app.ui.theme.KaktusGreen
import kotlinx.coroutines.delay

//private val Icons.Filled.Eco: ImageVector


@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit // Callback quando finisce il tempo
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animazione di opacità e scala (0 -> 1)
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), // Dura 2 secondi
        label = "AlphaAnim"
    )

    // Timer: parte l'animazione, aspetta, poi chiude
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Aspetta 2.5 secondi totali
        onAnimationFinished() // Dice alla MainActivity: "Ho finito!"
    }

    // Design della schermata
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KaktusBeige),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Cerchio verde con icona
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(alphaAnim.value) // Si ingrandisce
                    .alpha(alphaAnim.value) // Diventa visibile
                    .background(KaktusGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_kaktus),
                    contentDescription = "Logo Kaktus",
                    modifier = Modifier.size(100.dp) // Puoi ingrandirlo se vuoi, es. 100.dp o 120.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Testo Kaktus
            Text(
                text = "KAKTUS",
                color = KaktusGreen,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alphaAnim.value)
            )
        }
    }
}