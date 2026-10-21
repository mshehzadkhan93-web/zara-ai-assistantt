package com.captain.aiassistant.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captain.aiassistant.ui.theme.AvatarBg
import com.captain.aiassistant.ui.theme.CardWhite
import com.captain.aiassistant.ui.theme.GradientBlue
import com.captain.aiassistant.ui.theme.GradientPink
import com.captain.aiassistant.ui.theme.GradientPinkLight
import com.captain.aiassistant.ui.theme.GradientPurple
import com.captain.aiassistant.ui.theme.InkText

/**
 * Design B shared building blocks: vibrant blue -> purple -> pink
 * gradient background, white rounded cards, colorful quick-action chips,
 * the cute assistant avatar and the big friendly mic button.
 */

/** Full-screen gradient backdrop: blue (top) -> purple -> pink (bottom). */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(GradientBlue, GradientPurple, GradientPink)
            )
        )
    ) {
        // Soft light blobs for the playful wavy feel of the mockup.
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopStart)
                .offset(x = (-90).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 90.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GradientPinkLight.copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        content()
    }
}

/** White rounded card with a soft shadow — the main Design B surface. */
@Composable
fun WhiteCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(24.dp), clip = false)
            .background(CardWhite, RoundedCornerShape(24.dp)),
        content = content
    )
}

/** Rounded button filled with a blue -> purple gradient. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GradientBlue, GradientPurple)
                )
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 22.dp, vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Colorful pill chip (orange / teal / purple / pink) with an icon + label. */
@Composable
fun QuickActionChip(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Cute rounded assistant avatar: a soft blob with a simple smiley face. */
@Composable
fun AssistantAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(88.dp)
            .shadow(10.dp, RoundedCornerShape(32.dp), clip = false)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFBFDBFE), AvatarBg, Color(0xFFE9D5FF))
                ),
                RoundedCornerShape(32.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(52.dp)) {
            val eyeY = size.height * 0.38f
            val eyeR = size.width * 0.07f
            drawCircle(
                color = InkText,
                radius = eyeR,
                center = Offset(size.width * 0.34f, eyeY)
            )
            drawCircle(
                color = InkText,
                radius = eyeR,
                center = Offset(size.width * 0.66f, eyeY)
            )
            // Smile arc.
            drawArc(
                color = InkText,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(size.width * 0.28f, size.height * 0.42f),
                size = androidx.compose.ui.geometry.Size(
                    size.width * 0.44f,
                    size.height * 0.40f
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = size.width * 0.045f,
                    cap = StrokeCap.Round
                )
            )
            // Blush dots.
            drawCircle(
                color = GradientPink.copy(alpha = 0.55f),
                radius = eyeR * 1.1f,
                center = Offset(size.width * 0.20f, size.height * 0.58f)
            )
            drawCircle(
                color = GradientPink.copy(alpha = 0.55f),
                radius = eyeR * 1.1f,
                center = Offset(size.width * 0.80f, size.height * 0.58f)
            )
        }
    }
}

/** Voice button animation state. */
enum class OrbState { IDLE, LISTENING, SPEAKING }

/**
 * Big friendly gradient microphone button. It gently breathes when idle
 * and pulses strongly while listening or speaking. Tap it to talk.
 */
@Composable
fun VoiceMicButton(
    state: OrbState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "micPulse")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulse"
    )
    val amplitude = when (state) {
        OrbState.IDLE -> 0.04f
        OrbState.LISTENING -> 0.12f
        OrbState.SPEAKING -> 0.16f
    }
    val scale = 1f + pulse * amplitude
    val haloAlpha = 0.30f + pulse * 0.35f

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft halo ring.
        Box(
            modifier = Modifier
                .size((220 * scale).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = haloAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        // Gradient mic core.
        Box(
            modifier = Modifier
                .size((150 * scale).dp)
                .shadow(20.dp, CircleShape, clip = false)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientBlue, GradientPurple, GradientPink)
                    ),
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = "Speak",
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
