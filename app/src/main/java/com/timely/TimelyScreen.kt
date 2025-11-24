package com.timely

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimelyApp() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDate by remember { mutableStateOf(getCurrentDate()) }
    var previousMinute by remember { mutableStateOf(getCurrentMinute()) }
    var animateMinute by remember { mutableStateOf(false) }
    val (offsetX, offsetY) = rememberAntiBurnInOffset()

    val scale by animateFloatAsState(
        targetValue = if (animateMinute) 1.15f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            if (animateMinute) {
                animateMinute = false
            }
        },
        label = "scale"
    )

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            val newTime = getCurrentTime()
            val newMinute = getCurrentMinute()

            if (newMinute != previousMinute) {
                animateMinute = true
                previousMinute = newMinute
            }

            currentTime = newTime
            currentDate = getCurrentDate()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.antiBurnIn(offsetX, offsetY),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 144.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentDate,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            MusicVisualizer(
                modifier = Modifier.fillMaxWidth(),
                barCount = 64,
                barColor = Color.White
            )
        }
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

fun getCurrentMinute(): String {
    val sdf = SimpleDateFormat("mm", Locale.getDefault())
    return sdf.format(Date())
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd EEEE MMMM", Locale.ENGLISH)
    return sdf.format(Date())
}