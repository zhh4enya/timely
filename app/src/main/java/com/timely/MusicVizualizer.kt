package com.timely

import android.media.audiofx.Visualizer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun MusicVisualizer(
    modifier: Modifier = Modifier,
    barCount: Int = 64,
    barColor: Color = Color.White
) {
    var audioData by remember { mutableStateOf(ByteArray(0)) }
    var visualizer: Visualizer? by remember { mutableStateOf(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "idle")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    DisposableEffect(Unit) {
        try {
            visualizer = Visualizer(0).apply {
                captureSize = 1024

                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer?,
                            waveform: ByteArray?,
                            samplingRate: Int
                        ) {
                            waveform?.let {
                                audioData = it.clone()
                            }
                        }

                        override fun onFftDataCapture(
                            visualizer: Visualizer?,
                            fft: ByteArray?,
                            samplingRate: Int
                        ) {}
                    },
                    Visualizer.getMaxCaptureRate(),
                    true,
                    false
                )

                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            try {
                visualizer?.apply {
                    enabled = false
                    release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxWidth().height(40.dp)) {
        val width = size.width
        val height = size.height
        val barWidth = width / barCount
        val bottom = height

        if (audioData.isNotEmpty()) {
            val step = audioData.size / barCount

            for (i in 0 until barCount) {
                val index = (i * step).coerceIn(0, audioData.size - 1)
                val byte = audioData[index]
                val amplitude = abs(byte.toInt()) / 128f
                val barHeight = (amplitude * height * 0.7f).coerceAtLeast(3f)
                val x = i * barWidth + barWidth / 2

                drawLine(
                    color = barColor,
                    start = Offset(x, bottom - barHeight),
                    end = Offset(x, bottom),
                    strokeWidth = (barWidth * 0.4f).coerceAtLeast(2f),
                    cap = StrokeCap.Round
                )
            }
        } else {
            for (i in 0 until barCount) {
                val phase = (i.toFloat() / barCount) * Math.PI * 2
                val idleHeight = 3f + (kotlin.math.sin(phase + animatedProgress * Math.PI * 2).toFloat() * 1.5f)
                val x = i * barWidth + barWidth / 2

                drawLine(
                    color = barColor.copy(alpha = 0.3f),
                    start = Offset(x, bottom - idleHeight),
                    end = Offset(x, bottom),
                    strokeWidth = (barWidth * 0.4f).coerceAtLeast(2f),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

