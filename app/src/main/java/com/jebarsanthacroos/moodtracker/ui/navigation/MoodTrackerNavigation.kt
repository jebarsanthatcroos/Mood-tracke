package com.jebarsanthacroos.moodtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jebarsanthacroos.moodtracker.ui.MoodViewModel
import com.jebarsanthacroos.moodtracker.ui.screens.DetailScreen
import com.jebarsanthacroos.moodtracker.ui.screens.MainScreen
import com.jebarsanthacroos.moodtracker.ui.screens.WeeklyReportScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Detail : Screen("detail/{entryId}") {
        fun createRoute(entryId: Long) = "detail/$entryId"
    }
    object WeeklyReport : Screen("weekly_report")
}

@Composable
fun MoodTrackerNavigation(viewModel: MoodViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToDetail = { entryId ->
                    navController.navigate(Screen.Detail.createRoute(entryId))
                },
                onNavigateToReport = {
                    navController.navigate(Screen.WeeklyReport.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            DetailScreen(
                entryId = entryId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WeeklyReport.route) {
            WeeklyReportScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}