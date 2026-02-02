package com.habit.habitshare.presentation.screens.checkin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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

@Composable
fun StatusSelector(
    selectedStatus: CheckInStatus?,
    onStatusSelected: (CheckInStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusOption(
                icon = "✓",
                label = "Success",
                color = HabitShareColors.Success,
                isSelected = selectedStatus == CheckInStatus.SUCCESS,
                onClick = { onStatusSelected(CheckInStatus.SUCCESS) }
            )

            StatusOption(
                icon = "✗",
                label = "Fail",
                color = HabitShareColors.Fail,
                isSelected = selectedStatus == CheckInStatus.FAIL,
                onClick = { onStatusSelected(CheckInStatus.FAIL) }
            )

            StatusOption(
                icon = "−",
                label = "Skip",
                color = HabitShareColors.Skip,
                isSelected = selectedStatus == CheckInStatus.SKIP,
                onClick = { onStatusSelected(CheckInStatus.SKIP) }
            )

            StatusOption(
                icon = "🗑",
                label = "Clear",
                color = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                isSelected = selectedStatus == null,
                onClick = { onStatusSelected(null) }
            )
        }
    }
}

@Composable
private fun StatusOption(
    icon: String,
    label: String,
    color: Color,
    textColor: Color = Color.White,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) color else color.copy(alpha = 0.2f)
    val borderColor = if (isSelected) color else Color.Transparent
    val contentColor = if (isSelected) textColor else color

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(12.dp))
                } else Modifier
            )
            .background(backgroundColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
    }
}
