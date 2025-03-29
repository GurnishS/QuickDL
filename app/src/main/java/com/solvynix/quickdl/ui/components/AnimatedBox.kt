package com.solvynix.quickdl.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.4f),
        Color.Gray.copy(alpha = 0.2f),
        Color.Gray.copy(alpha = 0.4f)
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f, // Adjust for effect speed
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerAnim"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 500f, 0f) // Moves the shimmer effect
    )
    return brush
}

@Composable
fun AnimatedBox(width: Dp = 100.dp, height: Dp = 100.dp, brush: Brush = shimmerBrush()) {
    Box(
        modifier = Modifier
            .size(width, height)
            .background(brush, shape = RoundedCornerShape(2.dp))
    )
}

@Preview
@Composable
fun AnimatedBoxPreview() {
    AnimatedBox()
}