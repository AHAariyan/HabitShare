package com.habit.habitshare.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.habit.habitshare.presentation.screens.calendar.CalendarScreen
import com.habit.habitshare.presentation.screens.checkin.CheckInScreen
import com.habit.habitshare.presentation.screens.create.CreateHabitScreen
import com.habit.habitshare.presentation.screens.edit.EditHabitScreen
import com.habit.habitshare.presentation.screens.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreateHabit = {
                    navController.navigate(Screen.CreateHabit.route)
                },
                onNavigateToEditHabit = { habitId ->
                    navController.navigate(Screen.EditHabit.createRoute(habitId))
                },
                onNavigateToCheckIn = { habitId, date ->
                    navController.navigate(Screen.CheckIn.createRoute(habitId, date))
                },
                onNavigateToCalendar = { habitId ->
                    navController.navigate(Screen.Calendar.createRoute(habitId))
                }
            )
        }

        composable(Screen.CreateHabit.route) {
            CreateHabitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.LongType }
            )
        ) {
            EditHabitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CheckIn.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.LongType },
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            CheckInScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Calendar.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.LongType }
            )
        ) {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { habitId ->
                    navController.navigate(Screen.EditHabit.createRoute(habitId))
                },
                onNavigateToCheckIn = { habitId, date ->
                    navController.navigate(Screen.CheckIn.createRoute(habitId, date))
                }
            )
        }
    }
}
