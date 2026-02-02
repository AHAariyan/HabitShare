package com.habit.habitshare.presentation.screens.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.habitshare.domain.model.CheckIn
import com.habit.habitshare.ui.theme.HabitShareColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthGrid(
    yearMonth: YearMonth,
    checkIns: Map<LocalDate, CheckIn>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // Find the first day to display (might be from previous month)
    val firstDayOfWeek = DayOfWeek.SUNDAY
    var startDate = firstDayOfMonth
    while (startDate.dayOfWeek != firstDayOfWeek) {
        startDate = startDate.minusDays(1)
    }

    // Find the last day to display (might be from next month)
    var endDate = lastDayOfMonth
    while (endDate.dayOfWeek != DayOfWeek.SATURDAY) {
        endDate = endDate.plusDays(1)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Day of week headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("SU", "MO", "TU", "WE", "TH", "FR", "SA").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = HabitShareColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Calendar grid
        var currentDate = startDate
        while (currentDate <= endDate) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) {
                    val date = currentDate
                    val isCurrentMonth = date.month == yearMonth.month
                    val checkIn = checkIns[date]

                    DayCell(
                        date = date,
                        status = checkIn?.status,
                        hasComment = !checkIn?.comment.isNullOrEmpty(),
                        isToday = date == today,
                        isCurrentMonth = isCurrentMonth,
                        isSelected = date == selectedDate,
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.weight(1f)
                    )

                    currentDate = currentDate.plusDays(1)
                }
            }
        }
    }
}
