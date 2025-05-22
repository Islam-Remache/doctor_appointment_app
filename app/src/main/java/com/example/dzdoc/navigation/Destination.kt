package com.example.dzdoc.navigation

sealed class AppDestination(val route: String) {
    object AppointmentsList : AppDestination("appointments_list")
    object PatientsAppointmentsList : AppDestination("patients_appointments_list")

    object AppointmentDetails : AppDestination("appointment_details/{doctorId}") {
        fun createRoute(doctorId: String): String = "appointment_details/$doctorId"
    }
    object PatientAppointmentDetails : AppDestination("patient_appointment_details/{appointmentId}") {
        fun createRoute(appointmentId: Int): String = "patient_appointment_details/$appointmentId"
    }
    object DoctorViewPatientDetails : AppDestination("doctor_view_patient_details/{patientId}/{patientName}") {
        fun createRoute(patientId: Int, patientName: String): String = "doctor_view_patient_details/$patientId/$patientName"
    }
    object QRCode : AppDestination("qr_code/{appointmentId}") {
        fun createRoute(appointmentId: String): String = "qr_code/$appointmentId"
    }
    object Notifications : AppDestination("doctor_view_notifications/{doctorId}/{doctorName}") {
        fun createRoute(doctorId: Int, doctorName: String): String = "doctor_view_notifications/$doctorId/$doctorName"
    }
}