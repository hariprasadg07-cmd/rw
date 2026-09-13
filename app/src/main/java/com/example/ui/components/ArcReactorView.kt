package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkBackground
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisStarkGold
import com.example.voice.AssistantState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorView(
    state: AssistantState,
    rmsLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 210.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcReactorTransitions")

    // Clockwise rotation for outer tick ring
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (state == AssistantState.PROCESSING) 4000 else 18000, easing = LinearEasing)
        ),
        label = "OuterRingRotation"
    )

    // Counter-clockwise rotation for segmented ring
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (state == AssistantState.PROCESSING) 3000 else 12000, easing = LinearEasing)
        ),
        label = "InnerRingRotation"
    )

    // Idle breathing pulse
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdleBreathing"
    )

    // Dynamic color shifts depending on current operational state
    val coreColor = when (state) {
        AssistantState.LISTENING -> JarvisCyan
        AssistantState.PROCESSING -> JarvisStarkGold
        AssistantState.SPEAKING -> JarvisElectricBlue
        AssistantState.ERROR -> JarvisAlertRed
        AssistantState.IDLE -> JarvisCyan
    }

    val glowAlpha = when (state) {
        AssistantState.LISTENING -> (0.35f + rmsLevel * 0.45f).coerceIn(0.35f, 0.95f)
        AssistantState.PROCESSING -> 0.75f
        AssistantState.SPEAKING -> 0.65f
        AssistantState.ERROR -> 0.70f
        AssistantState.IDLE -> 0.25f
    }

    val dynamicScale = when (state) {
        AssistantState.LISTENING -> (1.0f + rmsLevel * 0.22f).coerceIn(0.98f, 1.25f)
        AssistantState.PROCESSING -> 1.05f
        AssistantState.SPEAKING -> 1.04f
        AssistantState.ERROR -> 0.98f
        AssistantState.IDLE -> idlePulse
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = coreColor),
                onClick = onClick
            )
            .testTag("arc_reactor_touch_target"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) - 12.dp.toPx()

            // 1. Ambient Glow Halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreColor.copy(alpha = glowAlpha),
                        coreColor.copy(alpha = glowAlpha * 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.25f * dynamicScale
                ),
                radius = radius * 1.2f * dynamicScale,
                center = center
            )

            // 2. Dark Titanium Center Well
            drawCircle(
                color = JarvisDarkBackground.copy(alpha = 0.92f),
                radius = radius * 0.88f,
                center = center
            )

            // 3. Outer Continuous Arc Ring
            drawCircle(
                color = coreColor.copy(alpha = 0.35f),
                radius = radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 4. Rotating Outer Ticks / Segments
            rotate(outerRotation, pivot = center) {
                val tickCount = 24
                for (i in 0 until tickCount) {
                    val angle = (i * 360f / tickCount) * (Math.PI / 180.0)
                    val rInner = radius - 6.dp.toPx()
                    val rOuter = radius + if (i % 3 == 0) 4.dp.toPx() else 1.dp.toPx()
                    val start = Offset(
                        center.x + (rInner * cos(angle)).toFloat(),
                        center.y + (rInner * sin(angle)).toFloat()
                    )
                    val end = Offset(
                        center.x + (rOuter * cos(angle)).toFloat(),
                        center.y + (rOuter * sin(angle)).toFloat()
                    )
                    drawLine(
                        color = if (i % 3 == 0) coreColor else coreColor.copy(alpha = 0.4f),
                        start = start,
                        end = end,
                        strokeWidth = if (i % 3 == 0) 2.dp.toPx() else 1.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // 5. Middle Segmented Reactor Ring (Counter-rotating)
            rotate(innerRotation, pivot = center) {
                val segmentRadius = radius * 0.72f
                val segments = 8
                val sweep = 30f
                for (i in 0 until segments) {
                    val startAngle = i * (360f / segments)
                    drawArc(
                        color = if (state == AssistantState.PROCESSING) JarvisStarkGold else coreColor.copy(alpha = 0.85f),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(center.x - segmentRadius, center.y - segmentRadius),
                        size = androidx.compose.ui.geometry.Size(segmentRadius * 2, segmentRadius * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // 6. Inner Glowing Core Ring
            val coreRingRadius = radius * 0.46f * dynamicScale
            drawCircle(
                color = coreColor.copy(alpha = 0.9f),
                radius = coreRingRadius,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // 7. Central Glowing Nucleus
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        coreColor,
                        coreColor.copy(alpha = 0.5f)
                    ),
                    center = center,
                    radius = coreRingRadius * 0.65f
                ),
                radius = coreRingRadius * 0.65f,
                center = center
            )
        }

        // Center Icon Indicator for intuitive control feedback
        val iconColor = when (state) {
            AssistantState.LISTENING -> JarvisDarkBackground
            AssistantState.PROCESSING -> JarvisDarkBackground
            AssistantState.SPEAKING -> JarvisDarkBackground
            else -> JarvisDarkBackground
        }

        when (state) {
            AssistantState.LISTENING -> {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Listening to voice input",
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            AssistantState.PROCESSING -> {
                Icon(
                    imageVector = Icons.Rounded.Psychology,
                    contentDescription = "Processing query",
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            AssistantState.SPEAKING -> {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Jarvis speaking - tap to stop",
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            AssistantState.ERROR -> {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "System alert",
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            AssistantState.IDLE -> {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Tap to speak to Jarvis",
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
