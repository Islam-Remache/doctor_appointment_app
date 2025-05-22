package com.example.dzdoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dzdoc.ui.model.AppointmentTab


@Composable
fun AppointmentToggle(
    selectedTab: AppointmentTab,
    onTabSelected: (AppointmentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTabBackgroundColor = Color(0xFF0B8FAC)
    val unselectedTabBackgroundColor = Color(0xFFD2EBE7)
    val selectedTabTextColor = Color.White
    val unselectedTabTextColor = Color(0xFF93AFB5)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val isUpcomingSelected = selectedTab == AppointmentTab.UPCOMING
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isUpcomingSelected) selectedTabBackgroundColor else unselectedTabBackgroundColor
                    )
                    .clickable { onTabSelected(AppointmentTab.UPCOMING) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Upcoming",
                    color = if (isUpcomingSelected) selectedTabTextColor else unselectedTabTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            val isPastSelected = selectedTab == AppointmentTab.PAST
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isPastSelected) selectedTabBackgroundColor else unselectedTabBackgroundColor
                    )
                    .clickable { onTabSelected(AppointmentTab.PAST) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Past",
                    color = if (isPastSelected) selectedTabTextColor else unselectedTabTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}