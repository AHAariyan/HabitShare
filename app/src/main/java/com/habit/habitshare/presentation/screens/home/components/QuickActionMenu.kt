package com.habit.habitshare.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.habit.habitshare.domain.model.CheckInStatus
import com.habit.habitshare.ui.theme.HabitShareColors

@Composable
fun QuickActionMenu(
    currentStatus: CheckInStatus?,
    onAction: (CheckInStatus) -> Unit,
    onClear: () -> Unit,
    onMore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCommentField by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf("") }

    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            modifier = modifier
                .shadow(8.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Success button (green check)
                    ActionButton(
                        backgroundColor = HabitShareColors.Success,
                        onClick = { onAction(CheckInStatus.SUCCESS) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Fail button (red X)
                    ActionButton(
                        backgroundColor = HabitShareColors.Fail,
                        onClick = { onAction(CheckInStatus.FAIL) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fail",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Skip button (gray minus)
                    ActionButton(
                        backgroundColor = HabitShareColors.Skip,
                        onClick = { onAction(CheckInStatus.SKIP) }
                    ) {
                        Text(
                            text = "—",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // More button (dots)
                    ActionButton(
                        backgroundColor = HabitShareColors.DayUnselected,
                        onClick = { showCommentField = !showCommentField }
                    ) {
                        Text(
                            text = "•••",
                            color = HabitShareColors.TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Comment field (shown when dots are clicked)
                if (showCommentField) {
                    BasicTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(
                                HabitShareColors.SurfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = HabitShareColors.TextPrimary
                        ),
                        cursorBrush = SolidColor(HabitShareColors.Primary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (comment.isEmpty()) {
                                    Text(
                                        text = "Add comment...",
                                        color = HabitShareColors.TextHint,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
