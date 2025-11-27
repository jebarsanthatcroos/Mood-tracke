package com.jebarsanthacroos.moodtracker.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jebarsanthacroos.moodtracker.data.MoodEntry
import com.jebarsanthacroos.moodtracker.ui.MoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MoodViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToReport: () -> Unit
) {
    val moodEntries by viewModel.moodEntries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Mood Tracker") },
                actions = {
                    IconButton(onClick = onNavigateToReport) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Weekly Report"
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
            // Mood Selector Section
            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MoodSelector(
                moods = viewModel.availableMoods,
                onMoodSelected = { mood -> viewModel.addMoodEntry(mood) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mood History Section
            Text(
                text = "Mood History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (moodEntries.isEmpty()) {
                EmptyState()
            } else {
                MoodHistory(
                    entries = moodEntries,
                    onEntryClick = onNavigateToDetail,
                    onDeleteEntry = { entry -> viewModel.deleteMoodEntry(entry) }
                )
            }
        }
    }
}

@Composable
fun MoodSelector(
    moods: List<String>,
    onMoodSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        moods.chunked(3).forEach { rowMoods ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowMoods.forEach { mood ->
                    MoodCard(
                        mood = mood,
                        onClick = { onMoodSelected(mood) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row is incomplete
                repeat(3 - rowMoods.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MoodCard(
    mood: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emoji = mood.split(" ").last()
    val name = mood.split(" ").first()

    val cardColor = when (name.lowercase()) {
        "happy" -> MaterialTheme.colorScheme.tertiaryContainer
        "calm" -> MaterialTheme.colorScheme.secondaryContainer
        "neutral" -> MaterialTheme.colorScheme.surfaceVariant
        "sad" -> MaterialTheme.colorScheme.primaryContainer
        "anxious" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📝",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No moods logged yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MoodHistory(
    entries: List<MoodEntry>,
    onEntryClick: (Long) -> Unit,
    onDeleteEntry: (MoodEntry) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(entries, key = { it.id }) { entry ->
            MoodHistoryItem(
                entry = entry,
                onClick = { onEntryClick(entry.id) },
                onDelete = { onDeleteEntry(entry) }
            )
        }
    }
}

@Composable
fun MoodHistoryItem(
    entry: MoodEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.mood,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.getFormattedTimestamp(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (entry.note.isNotEmpty()) {
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete mood entry",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}