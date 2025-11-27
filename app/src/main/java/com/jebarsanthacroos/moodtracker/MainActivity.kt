package com.jebarsanthacroos.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jebarsanthacroos.moodtracker.data.MoodDatabase
import com.jebarsanthacroos.moodtracker.data.MoodRepository
import com.jebarsanthacroos.moodtracker.ui.MoodViewModel
import com.jebarsanthacroos.moodtracker.ui.MoodViewModelFactory
import com.jebarsanthacroos.moodtracker.ui.navigation.MoodTrackerNavigation
import com.jebarsanthacroos.moodtracker.ui.theme.MoodTrackerTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database and repository
        val database = MoodDatabase.getDatabase(applicationContext)
        val repository = MoodRepository(database.moodDao())
        val viewModelFactory = MoodViewModelFactory(repository)

        setContent {
            MoodTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MoodViewModel = viewModel(factory = viewModelFactory)
                    MoodTrackerNavigation(viewModel = viewModel)
                }
            }
        }
    }
}