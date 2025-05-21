package com.example.doctor_appointment_app.ui.screens.common.auth.resetPassword

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle


@Composable
fun VerifyCodeScreen(navController: NavController) {
    val codeDigits by remember { mutableStateOf(listOf("", "", "", "")) }
    var codeDigit1 by remember { mutableStateOf("0") }
    var codeDigit2 by remember { mutableStateOf("0") }
    var codeDigit3 by remember { mutableStateOf("0") }
    var codeDigit4 by remember { mutableStateOf("0") }

    // Countdown timer
    var remainingSeconds by remember { mutableStateOf(59) }
    val animatedTimer by animateIntAsState(
        targetValue = remainingSeconds,
        label = "timer"
    )

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        // Illustration
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(LightBlueBackground),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = com.example.doctor_appointment_app.R.drawable.illustration),
                contentDescription = "Check Email",
                modifier = Modifier
                    .size(160.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Check your mail",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "We just sent a verification code to your registered email address",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Verification code input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            VerificationCodeDigit(codeDigit1, { codeDigit1 = it })
            VerificationCodeDigit(codeDigit2, { codeDigit2 = it })
            VerificationCodeDigit(codeDigit3, { codeDigit3 = it })
            VerificationCodeDigit(codeDigit4, { codeDigit4 = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer
        Text(
            text = "We will resend the code in $animatedTimer s",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Verify button
        Button(
            onClick = { navController.navigate(ResetPasswordScreen.NEW_PASSWORD.name) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            )
        ) {
            Text(
                text = "Verify",
                fontSize = 16.sp
            )
        }
    }
}





@Composable
fun VerificationCodeDigit(value: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= 1 && it.all { c -> c.isDigit() }) {
                    onValueChange(it)
                }
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}
