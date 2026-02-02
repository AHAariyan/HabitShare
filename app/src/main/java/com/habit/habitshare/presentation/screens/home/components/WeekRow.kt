package com.habit.habitshare.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.LocalDate

@Composable
fun WeekRow(
    checkIns: Map<LocalDate, CheckInStatus>,
    onDayClick: (LocalDate) -> Unit,
    onQuickAction: (LocalDate, CheckInStatus) -> Unit,
    onClearAction: (LocalDate) -> Unit,
    onMoreClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    // Show last 7 days ending with today
    val weekDays = (6 downTo 0).map { today.minusDays(it.toLong()) }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { date ->
            val status = checkIns[date]
            val isToday = date == today
            val hasComment = false // TODO: Add comment tracking to checkIns

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    DayCircle(
                        date = date,
                        status = status,
                        isToday = isToday,
                        hasComment = hasComment,
                        onClick = {
                            selectedDate = if (selectedDate == date) null else date
                        }
                    )

                    // Quick action menu
                    if (selectedDate == date) {
                        QuickActionMenu(
                            currentStatus = status,
                            onAction = { actionStatus ->
                                onQuickAction(date, actionStatus)
                                selectedDate = null
                            },
                            onClear = {
                                onClearAction(date)
                                selectedDate = null
                            },
                            onMore = {
                                onMoreClick(date)
                                selectedDate = null
                            },
                            onDismiss = { selectedDate = null }
                        )
                    }
                }

                // TODAY label
                if (isToday) {
                    Text(
                        text = "TODAY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = HabitShareColors.TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else {
                    Box(modifier = Modifier.height(13.dp))
                }
            }
        }
    }
}

@Composable
private fun DayCircle(
    date: LocalDate,
    status: CheckInStatus?,
    isToday: Boolean,
    hasComment: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        CheckInStatus.SUCCESS -> HabitShareColors.Success
        CheckInStatus.FAIL -> HabitShareColors.Fail
        CheckInStatus.SKIP -> HabitShareColors.Skip
        null -> HabitShareColors.DayUnselected
    }

    val textColor = when (status) {
        CheckInStatus.SUCCESS, CheckInStatus.FAIL -> Color.White
        CheckInStatus.SKIP -> Color.White
        null -> HabitShareColors.TextSecondary
    }

    // Get day abbreviation (Mo, Tu, We, Th, Fr, Sa, Su)
    val dayAbbreviation = when (date.dayOfWeek.value) {
        1 -> "Mo"
        2 -> "Tu"
        3 -> "We"
        4 -> "Th"
        5 -> "Fr"
        6 -> "Sa"
        7 -> "Su"
        else -> ""
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayAbbreviation,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            // Show dots for comment indicator
            if (hasComment) {
                Text(
                    text = "...",
                    color = textColor,
                    fontSize = 8.sp,
                    lineHeight = 6.sp
                )
            }
        }
    }
}
