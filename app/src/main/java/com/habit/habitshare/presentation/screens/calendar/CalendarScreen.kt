package com.habit.habitshare.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habit.habitshare.presentation.screens.calendar.components.MonthGrid
import com.habit.habitshare.presentation.screens.calendar.components.MonthHeader
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToCheckIn: (Long, String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle error messages
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
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Stats screen - not implemented */ }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Stats",
                            tint = Color.White
                        )
                    }
                    TextButton(onClick = { onNavigateToEdit(uiState.habitId) }) {
                        Text(
                            text = "Edit",
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
        if (uiState.isLoading && uiState.habitTitle.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HabitShareColors.Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                // Habit title and stats
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = uiState.habitTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HabitShareColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Streak: +${uiState.streak}  |  Overall: ${uiState.overallPercentage.toInt()}%  |  Private",
                        fontSize = 13.sp,
                        color = HabitShareColors.TextSecondary
                    )
                }

                HorizontalDivider(color = HabitShareColors.Divider)

                // Month navigation
                MonthHeader(
                    currentMonth = uiState.currentMonth,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth
                )

                // Calendar grid
                MonthGrid(
                    yearMonth = uiState.currentMonth,
                    checkIns = uiState.checkIns,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { date ->
                        viewModel.selectDate(date)
                    }
                )

                HorizontalDivider(color = HabitShareColors.Divider)

                // Comment section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val selectedCheckIn = uiState.selectedDate?.let { uiState.checkIns[it] }
                    val comment = selectedCheckIn?.comment

                    if (uiState.selectedDate != null) {
                        val dateStr = uiState.selectedDate!!.dayOfMonth.toString()
                        if (comment.isNullOrBlank()) {
                            Text(
                                text = "No Comments",
                                fontSize = 14.sp,
                                color = HabitShareColors.TextSecondary
                            )
                        } else {
                            Text(
                                text = "$dateStr.  $comment",
                                fontSize = 14.sp,
                                color = HabitShareColors.TextPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "No Comments",
                            fontSize = 14.sp,
                            color = HabitShareColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
