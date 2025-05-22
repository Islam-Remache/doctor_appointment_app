package com.example.dzdoc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.data.remote.RetrofitInstance
import com.example.dzdoc.data.repository.AppointmentRepositoryImpl
import com.example.dzdoc.navigation.AppDestination
import com.example.dzdoc.ui.components.*
import com.example.dzdoc.ui.model.AppointmentTab
import com.example.dzdoc.ui.model.DoctorScreenAppointmentItem
import com.example.dzdoc.ui.viewmodel.AppointmentUpdateEvent
import com.example.dzdoc.ui.viewmodel.DoctorAppointmentsUiState
import com.example.dzdoc.ui.viewmodel.DoctorAppointmentsViewModel
import com.example.dzdoc.ui.viewmodel.DoctorAppointmentsViewModelFactory

@Composable
fun PatientAppointmentsListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val repository = remember { AppointmentRepositoryImpl(RetrofitInstance.api) }
    val viewModel: DoctorAppointmentsViewModel = viewModel(
        factory = DoctorAppointmentsViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val updateEvent by viewModel.updateEvent.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(updateEvent) {
        when (val event = updateEvent) {
            is AppointmentUpdateEvent.Success -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateEvent()
            }
            is AppointmentUpdateEvent.Error -> {
                Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                viewModel.resetUpdateEvent()
            }
            AppointmentUpdateEvent.Idle -> {}
        }
    }

    var selectedTab by remember { mutableStateOf(AppointmentTab.UPCOMING) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedStatuses by remember {
        mutableStateOf(setOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED))
    }

    LaunchedEffect(selectedTab) {
        selectedStatuses = when (selectedTab) {
            AppointmentTab.UPCOMING -> setOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
            AppointmentTab.PAST -> setOf(AppointmentStatus.COMPLETED, AppointmentStatus.DECLINED)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 20.dp)
        ) {
            AppHeader(title = "Patient Appointments", showBackButton = false)
            Spacer(modifier = Modifier.height(16.dp))
            AppointmentToggle(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(onFilterClick = { showFilterDialog = true })
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is DoctorAppointmentsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DoctorAppointmentsUiState.Success -> {
                    val appointmentsToDisplay = remember(state.appointments, searchQuery, selectedTab, selectedStatuses) {
                        filterDoctorAppointments(
                            appointments = state.appointments,
                            searchQuery = searchQuery,
                            selectedTab = selectedTab,
                            selectedStatuses = selectedStatuses
                        )
                    }
                    if (appointmentsToDisplay.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
                            Text("No patient appointments match your criteria.", color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    } else {
                        PatientAppointmentsListInternal(
                            appointments = appointmentsToDisplay,
                            onAccept = { appointmentItem ->
                                viewModel.confirmAppointment(appointmentItem.appointmentId)
                            },
                            onDecline = { appointmentItem ->
                                viewModel.declineAppointment(appointmentItem.appointmentId)
                            },
                            onAppointmentClick = { appointmentItem ->
                                navController.navigate(
                                    AppDestination.DoctorViewPatientDetails.createRoute(
                                        patientId = appointmentItem.patientId,
                                        patientName = appointmentItem.patientName
                                    )
                                )
                            }
                        )
                    }
                }
                is DoctorAppointmentsUiState.Error -> {
                    Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
                        Text("Error: ${state.message}", color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        if (showFilterDialog) {
            FilterDialog(
                allAvailableStatuses = setOf(
                    AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED,
                    AppointmentStatus.COMPLETED, AppointmentStatus.DECLINED
                ),
                selectedStatuses = selectedStatuses,
                onStatusToggle = { status ->
                    selectedStatuses = if (selectedStatuses.contains(status)) selectedStatuses - status
                    else selectedStatuses + status
                },
                onDismissRequest = { showFilterDialog = false },
                onClearFilters = {
                    selectedStatuses = when (selectedTab) {
                        AppointmentTab.UPCOMING -> setOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                        AppointmentTab.PAST -> setOf(AppointmentStatus.COMPLETED, AppointmentStatus.DECLINED)
                    }
                    showFilterDialog = false
                },
                onApplyFilters = { showFilterDialog = false }
            )
        }
    }
}

private fun filterDoctorAppointments(
    appointments: List<DoctorScreenAppointmentItem>,
    searchQuery: String,
    selectedTab: AppointmentTab,
    selectedStatuses: Set<AppointmentStatus>
): List<DoctorScreenAppointmentItem> {
    return appointments.filter { item ->
        val matchesTab = when (selectedTab) {
            AppointmentTab.UPCOMING -> item.appointmentStatus == AppointmentStatus.PENDING || item.appointmentStatus == AppointmentStatus.CONFIRMED
            AppointmentTab.PAST -> item.appointmentStatus == AppointmentStatus.COMPLETED || item.appointmentStatus == AppointmentStatus.DECLINED
        }
        val matchesStatus = selectedStatuses.isEmpty() || selectedStatuses.contains(item.appointmentStatus)
        val matchesQuery = searchQuery.isEmpty() ||
                item.patientName.contains(searchQuery, ignoreCase = true) ||
                (item.reasonForVisit?.contains(searchQuery, ignoreCase = true) ?: false)
        matchesTab && matchesStatus && matchesQuery
    }.sortedBy { it.appointmentDate }
}
