package com.jebarsanthacroos.moodtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jebarsanthacroos.moodtracker.ui.theme.MoodTrackerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.forEach
import kotlin.jvm.java

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoodTrackerTheme { // Use your custom theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CleanSplashScreen(
                        onSplashComplete = {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CleanSplashScreen(onSplashComplete: () -> Unit) {
    // Animation states
    val logoScale = remember { Animatable(0.8f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition()
    val floatOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Start animations and navigation
    LaunchedEffect(Unit) {
        // Initial delay
        delay(300)

        // Parallel animations
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(800))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = tween(1000))
        }

        delay(500)
        textAlpha.animateTo(1f, animationSpec = tween(600))

        // Total splash time: 2.5 seconds
        delay(2500)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Background with subtle gradient using theme colors
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        radius = 800f
                    )
                )
        )

        // Subtle floating particles
        FloatingParticles(floatOffset = floatOffset.value)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            // Clean Logo - No background, just the logo itself
            Image(
                painter = painterResource(id = R.drawable.company),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(300.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .graphicsLayer {
                        GraphicsLayerScope.translationY = floatOffset.value * 8f
                    }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // App Name - Clean typography using theme colors
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Text(
                    text = "MoodTracker",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Track your daily emotions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.2.sp
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Loading dots using theme colors
            LoadingDots(
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Developer credit using theme colors
            Text(
                text = "By Jebarsan Thatcroos",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .alpha(textAlpha.value * 0.8f)
                    .offset(y = 20.dp),
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun FloatingParticles(floatOffset: Float) {
    // Subtle floating dots in background using theme colors
    val particles = listOf(
        Particle(offsetX = -150f, offsetY = -200f, size = 6.dp, delay = 0f),
        Particle(offsetX = 120f, offsetY = -150f, size = 4.dp, delay = 0.3f),
        Particle(offsetX = -100f, offsetY = 180f, size = 5.dp, delay = 0.6f),
        Particle(offsetX = 160f, offsetY = 100f, size = 3.dp, delay = 0.9f),
    )

    particles.forEach { particle ->
        Box(
            modifier = Modifier
                .size(particle.size)
                .offset(
                    x = particle.offsetX.dp,
                    y = (particle.offsetY + floatOffset * 15f).dp
                )
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun LoadingDots(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val dot1Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dot2Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dot3Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot 1
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = dot1Alpha.value),
                    shape = CircleShape
                )
        )

        // Dot 2
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = dot2Alpha.value),
                    shape = CircleShape
                )
        )

        // Dot 3
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = dot3Alpha.value),
                    shape = CircleShape
                )
        )
    }
}

// Data class for particles
data class Particle(
    val offsetX: Float,
    val offsetY: Float,
    val size: Dp,
    val delay: Float
)