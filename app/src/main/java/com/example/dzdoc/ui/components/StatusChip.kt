package com.example.dzdoc.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusChip(modifier : Modifier = Modifier, status : String) {

    val (backgroundColor, textColor) = when(status) {
        "CONFIRMED" -> Pair(Color(0xFF4CAF50), Color.White)
        "PENDING" -> Pair(Color(0xFF0B8FAC), Color.White)
        "DECLINED" -> Pair(Color(0xFFF44336), Color.White)
        "COMPLETED" -> Pair(Color(0xFF9C6E9F), Color.White)
        else -> Pair(Color.LightGray, Color.Black)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(2.23.dp)
    ) {

        Text(
            text = status,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }

}