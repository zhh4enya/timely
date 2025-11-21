package com.timely

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun rememberAntiBurnInOffset(): Pair<Float, Float> {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            offsetX = Random.nextInt(-25, 26).toFloat()
            offsetY = Random.nextInt(-25, 26).toFloat()
            delay(30000)
            offsetX = 0f
            offsetY = 0f
        }
    }

    return Pair(offsetX, offsetY)
}

fun Modifier.antiBurnIn(offsetX: Float, offsetY: Float): Modifier {
    return this.graphicsLayer {
        translationX = offsetX
        translationY = offsetY
    }
}