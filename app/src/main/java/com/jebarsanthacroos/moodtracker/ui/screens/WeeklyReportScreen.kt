package com.jebarsanthacroos.moodtracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jebarsanthacroos.moodtracker.ui.MoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit
) {
    val weeklyData by viewModel.weeklyMoodData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWeeklyData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Report") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Mood frequency over the past week",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (weeklyData.isEmpty() || weeklyData.values.all { it == 0 }) {
                EmptyReportState()
            } else {
                BarChart(
                    data = weeklyData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                MoodStatistics(data = weeklyData)
            }
        }
    }
}

@Composable
fun EmptyReportState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📊",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No data yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Start logging your moods to see your weekly report",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BarChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.values.maxOrNull() ?: 1
    val barColors = listOf(
        Color(0xFF4CAF50), // Happy - Green
        Color(0xFF2196F3), // Calm - Blue
        Color(0xFF9E9E9E), // Neutral - Gray
        Color(0xFFFF9800), // Sad - Orange
        Color(0xFFF44336)  // Anxious - Red
    )

    var animationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        animationPlayed = true
    }

    // Compute animated heights in Composable context
    val animatedHeights = data.values.mapIndexed { index, value ->
        val targetHeight = if (animationPlayed) value.toFloat() / maxValue else 0f
        animateFloatAsState(
            targetValue = targetHeight,
            animationSpec = tween(durationMillis = 800, delayMillis = index * 100),
            label = "bar_animation_$index"
        ).value
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barWidth = size.width / (data.size * 2)
            val spacing = barWidth * 0.5f

            data.entries.forEachIndexed { index, entry ->
                val animatedHeightPx = animatedHeights[index] * size.height * 0.8f
                val x = index * (barWidth + spacing) + spacing
                val y = size.height - animatedHeightPx

                // Draw bar
                drawRect(
                    color = barColors[index % barColors.size],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, animatedHeightPx)
                )

                // Draw value text
                if (entry.value > 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        entry.value.toString(),
                        x + barWidth / 2,
                        y - 10f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 40f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.keys.forEach { mood ->
                Text(
                    text = mood.split(" ").last(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.width(50.dp)
                )
            }
        }
    }
}

@Composable
fun MoodStatistics(data: Map<String, Int>) {
    val totalEntries = data.values.sum()
    val mostCommonMood = data.maxByOrNull { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Entries",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = totalEntries.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (mostCommonMood != null && mostCommonMood.value > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Most Common",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = mostCommonMood.key,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
