package com.habit.habitshare.presentation.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun DayCell(
    date: LocalDate,
    status: CheckInStatus?,
    hasComment: Boolean,
    isToday: Boolean,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !isCurrentMonth -> HabitShareColors.DayUnselected.copy(alpha = 0.3f)
        status == CheckInStatus.SUCCESS -> HabitShareColors.Success
        status == CheckInStatus.FAIL -> HabitShareColors.Fail
        status == CheckInStatus.SKIP -> HabitShareColors.Skip
        else -> HabitShareColors.DayUnselected
    }

    val textColor = when {
        !isCurrentMonth -> HabitShareColors.TextHint
        status != null -> Color.White
        else -> HabitShareColors.TextSecondary
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(enabled = isCurrentMonth, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                // Comment indicator dots
                if (hasComment && isCurrentMonth) {
                    Text(
                        text = "...",
                        fontSize = 8.sp,
                        color = textColor,
                        lineHeight = 6.sp
                    )
                }
            }
        }
    }
}
