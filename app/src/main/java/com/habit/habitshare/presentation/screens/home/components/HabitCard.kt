package com.habit.habitshare.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.presentation.screens.home.HabitWithStats
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.LocalDate

@Composable
fun HabitCard(
    habit: HabitWithStats,
    onCardClick: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    onQuickAction: (LocalDate, CheckInStatus) -> Unit,
    onClearAction: (LocalDate) -> Unit,
    onMoreClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .padding(16.dp)
    ) {
        // Habit title
        Text(
            text = habit.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HabitShareColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Stats line: "Streak: +1 | Overall: 16% | Private"
        Text(
            text = buildStatsLine(habit),
            fontSize = 13.sp,
            color = HabitShareColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Week row
        WeekRow(
            checkIns = habit.weekCheckIns,
            onDayClick = onDayClick,
            onQuickAction = onQuickAction,
            onClearAction = onClearAction,
            onMoreClick = onMoreClick
        )
    }
}

private fun buildStatsLine(habit: HabitWithStats): String {
    val streakText = "Streak: +${habit.streak}"
    val overallText = "Overall: ${habit.overallPercentage.toInt()}%"
    val privacyText = if (habit.isPrivate) "Private" else "Shared"
    return "$streakText  |  $overallText  |  $privacyText"
}
