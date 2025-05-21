package com.example.doctor_appointment_app.viewmodel

class ResetPasswordViewModel {
    // Email verification
    fun sendVerificationCode(email: String, onSuccess: () -> Unit) {
        // Implementation for sending verification code
        onSuccess()
    }

    // Verify code
    fun verifyCode(code: String, onSuccess: () -> Unit) {
        // Implementation for verifying code
        onSuccess()
    }

    // Reset password
    fun resetPassword(newPassword: String, onSuccess: () -> Unit) {
        // Implementation for resetting password
        onSuccess()
    }
}