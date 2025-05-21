package com.example.doctor_appointment_app.ui.screens.common.auth.resetPassword

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun PasswordResetFlow() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ResetPasswordScreen.FORGOT_PASSWORD.name
    ) {
        composable(ResetPasswordScreen.FORGOT_PASSWORD.name) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(ResetPasswordScreen.VERIFY_CODE.name) {
            VerifyCodeScreen(navController = navController)
        }
        composable(ResetPasswordScreen.NEW_PASSWORD.name) {
            NewPasswordScreen(navController = navController)
        }
        composable(ResetPasswordScreen.SUCCESS.name) {
            ResetSuccessScreen(navController = navController)
        }
    }
}


