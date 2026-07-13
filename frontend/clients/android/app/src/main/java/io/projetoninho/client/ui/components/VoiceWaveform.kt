package io.projetoninho.client.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VoiceWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val animValues = List(5) { index ->
        infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, delayMillis = index * 100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = Modifier
            .height(60.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animValues.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(anim.value.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}
