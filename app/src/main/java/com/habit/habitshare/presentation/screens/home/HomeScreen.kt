package com.habit.habitshare.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habit.habitshare.presentation.screens.home.components.HabitCard
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreateHabit: () -> Unit,
    onNavigateToEditHabit: (Long) -> Unit,
    onNavigateToCheckIn: (Long, String) -> Unit,
    onNavigateToCalendar: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val quickActionResult by viewModel.quickActionResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle quick action results
    LaunchedEffect(quickActionResult) {
        when (val result = quickActionResult) {
            is QuickActionResult.Success -> {
                snackbarHostState.showSnackbar("Logged ${result.status.name.lowercase()}")
                viewModel.clearQuickActionResult()
            }
            is QuickActionResult.Cleared -> {
                snackbarHostState.showSnackbar("Check-in cleared")
                viewModel.clearQuickActionResult()
            }
            is QuickActionResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.clearQuickActionResult()
            }
            null -> {}
        }
    }

    // Handle errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Habits",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    TextButton(onClick = onNavigateToCreateHabit) {
                        Text(
                            text = "Add",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HabitShareColors.Primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HabitShareColors.Primary)
                    }
                }

                uiState.habits.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                }

                else -> {
                    // Calculate overall percentage
                    val overallPercentage = if (uiState.habits.isNotEmpty()) {
                        uiState.habits.map { it.overallPercentage }.average().toInt()
                    } else 0

                    // ALL HABITS header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HabitShareColors.SurfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ALL HABITS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = HabitShareColors.TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "$overallPercentage%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = HabitShareColors.TextSecondary
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.habits,
                            key = { it.id }
                        ) { habit ->
                            HabitCard(
                                habit = habit,
                                onCardClick = { onNavigateToCalendar(habit.id) },
                                onDayClick = { date ->
                                    // Day click is handled by the quick action menu
                                },
                                onQuickAction = { date, status ->
                                    viewModel.quickCheckIn(habit.id, date, status)
                                },
                                onClearAction = { date ->
                                    viewModel.clearCheckIn(habit.id, date)
                                },
                                onMoreClick = { date ->
                                    onNavigateToCheckIn(
                                        habit.id,
                                        date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                    )
                                }
                            )
                            HorizontalDivider(color = HabitShareColors.Divider)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No habits yet",
            style = MaterialTheme.typography.headlineSmall,
            color = HabitShareColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap Add to create your first habit",
            style = MaterialTheme.typography.bodyMedium,
            color = HabitShareColors.TextHint,
            textAlign = TextAlign.Center
        )
    }
}
