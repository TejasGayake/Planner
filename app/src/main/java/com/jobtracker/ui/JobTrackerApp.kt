package com.jobtracker.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jobtracker.ui.screens.AddEditJobScreen
import com.jobtracker.ui.screens.CalendarScreen
import com.jobtracker.ui.screens.DashboardScreen
import com.jobtracker.ui.screens.JobDetailScreen
import com.jobtracker.ui.screens.SettingsScreen
import com.jobtracker.ui.theme.JobTrackerTheme

object Routes {
    const val DASHBOARD = "dashboard"
    const val ADD_JOB = "add"
    const val EDIT_JOB = "edit/{jobId}"
    const val JOB_DETAIL = "job/{jobId}"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    fun jobDetail(jobId: Long) = "job/$jobId"
    fun editJob(jobId: Long) = "edit/$jobId"
}

@Composable
fun JobTrackerApp(
    parsedJobJson: String? = null,
    imageUri: String? = null,
    startDestination: String = Routes.DASHBOARD
) {
    JobTrackerTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = spring(dampingRatio = 0.825f, stiffness = 300f),
                    initialOffsetX = { it / 3 }
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = spring(dampingRatio = 0.825f, stiffness = 300f),
                    initialOffsetX = { -it / 3 }
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(200),
                    targetOffsetX = { it / 3 }
                ) + fadeOut(animationSpec = tween(200))
            },
            modifier = Modifier
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigateToDetail = { jobId ->
                        navController.navigate(Routes.jobDetail(jobId))
                    },
                    onNavigateToAdd = {
                        navController.navigate(Routes.ADD_JOB)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Routes.SETTINGS)
                    },
                    onNavigateToCalendar = {
                        navController.navigate(Routes.CALENDAR)
                    }
                )
            }

            composable(
                route = Routes.ADD_JOB
            ) {
                AddEditJobScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onJobSaved = { jobId ->
                        // Navigate to detail after saving
                        navController.navigate(Routes.jobDetail(jobId)) {
                            popUpTo(Routes.DASHBOARD)
                        }
                    }
                )
            }

            composable(
                route = Routes.EDIT_JOB,
                arguments = listOf(navArgument("jobId") { type = NavType.LongType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getLong("jobId") ?: return@composable
                AddEditJobScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onJobSaved = { savedId ->
                        navController.navigate(Routes.jobDetail(savedId)) {
                            popUpTo(Routes.DASHBOARD)
                        }
                    }
                )
            }

            composable(
                route = Routes.JOB_DETAIL,
                arguments = listOf(navArgument("jobId") { type = NavType.LongType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getLong("jobId") ?: return@composable
                JobDetailScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditJob = { id ->
                        navController.navigate(Routes.editJob(id))
                    }
                )
            }

            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { jobId ->
                        navController.navigate(Routes.jobDetail(jobId))
                    }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
