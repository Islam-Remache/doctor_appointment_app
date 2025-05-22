package com.example.dzdoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.dzdoc.data.remote.RetrofitInstance
import com.example.dzdoc.data.repository.AppointmentRepositoryImpl
import com.example.dzdoc.navigation.AppDestination
import com.example.dzdoc.ui.components.*
import com.example.dzdoc.ui.model.PatientScreenAppointmentItem
import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.ui.model.AppointmentTab
import com.example.dzdoc.ui.viewmodel.PatientAppointmentsUiState
import com.example.dzdoc.ui.viewmodel.PatientAppointmentsViewModel
import com.example.dzdoc.ui.viewmodel.PatientAppointmentsViewModelFactory

@Composable
fun AppointmentsList(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val repository = remember { AppointmentRepositoryImpl(RetrofitInstance.api) }
    val viewModel: PatientAppointmentsViewModel = viewModel(
        factory = PatientAppointmentsViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableStateOf(AppointmentTab.UPCOMING) }
    var searchQuery by remember { mutableStateOf("") }
    var isDescendingSort by remember { mutableStateOf(true) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedStatuses by remember {
        mutableStateOf(setOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED, AppointmentStatus.DECLINED))
    }

    LaunchedEffect(selectedTab) {
        selectedStatuses = when (selectedTab) {
            AppointmentTab.UPCOMING -> setOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED, AppointmentStatus.DECLINED)
            AppointmentTab.PAST -> setOf(AppointmentStatus.COMPLETED, AppointmentStatus.CONFIRMED, AppointmentStatus.DECLINED)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 20.dp)
        ) {
            AppHeader(
                title = "My Schedule",
                notificationCount = 0,
                showBackButton = false,
                onNotificationClick = {
                    // Use the correct route with real doctorId and doctorName
                    val doctorId = 101
                    val doctorName = "DrSmith"
                    navController.navigate(
                        AppDestination.Notifications.createRoute(doctorId, doctorName)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppointmentToggle(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, modifier = Modifier.weight(1f))
                SortButton(isDescending = isDescendingSort, onToggleSort = { isDescendingSort = !isDescendingSort })
                FilterButton(onFilterClick = { showFilterDialog = true })
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is PatientAppointmentsUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is PatientAppointmentsUiState.Success -> {
                        val appointmentsToDisplay = remember(state.appointments, searchQuery, selectedTab, selectedStatuses, isDescendingSort) {
                            filterAndSortAppointments(
                                appointments = state.appointments,
                                searchQuery = searchQuery,
                                selectedTab = selectedTab,
                                selectedStatuses = selectedStatuses,
                                isDescendingSort = isDescendingSort
                            )
                        }
                        if (appointmentsToDisplay.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("No appointments found matching your criteria.", textAlign = TextAlign.Center)
                            }
                        } else {
                            DoctorAppointmentList(
                                appointments = appointmentsToDisplay,
                                onReschedule = { },
                                onCancel = { appointmentItem ->
                                    viewModel.cancelAppointmentFromList(appointmentItem.id)
                                },
                                onAppointmentClick = { clickedAppointmentItem ->
                                    navController.navigate(
                                        AppDestination.PatientAppointmentDetails.createRoute(clickedAppointmentItem.id)
                                    )
                                }
                            )
                        }
                    }
                    is PatientAppointmentsUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Error: ${state.message}", color = Color.Red, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        AddAppointmentFAB(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = if (true) 80.dp else 0.dp)
        )

        if (showFilterDialog) {
            FilterDialog(
                allAvailableStatuses = AppointmentStatus.values().toSet(),
                selectedStatuses = selectedStatuses,
                onStatusToggle = { status ->
                    selectedStatuses = if (selectedStatuses.contains(status)) selectedStatuses - status
                    else selectedStatuses + status
                },
                onDismissRequest = { showFilterDialog = false },
                onClearFilters = {
                    selectedStatuses = when (selectedTab) {
                        AppointmentTab.UPCOMING -> setOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED, AppointmentStatus.DECLINED)
                        AppointmentTab.PAST -> setOf(AppointmentStatus.COMPLETED)
                    }
                    showFilterDialog = false
                },
                onApplyFilters = { showFilterDialog = false }
            )
        }
    }
}

private fun filterAndSortAppointments(
    appointments: List<PatientScreenAppointmentItem>,
    searchQuery: String,
    selectedTab: AppointmentTab,
    selectedStatuses: Set<AppointmentStatus>,
    isDescendingSort: Boolean
): List<PatientScreenAppointmentItem> {
    return appointments.filter { item ->
        val matchesTab = when (selectedTab) {
            AppointmentTab.UPCOMING -> item.status != AppointmentStatus.COMPLETED
            AppointmentTab.PAST -> item.status == AppointmentStatus.COMPLETED
        }
        val matchesStatus = selectedStatuses.isEmpty() || selectedStatuses.contains(item.status)
        val matchesQuery = searchQuery.isEmpty() ||
                item.doctorName.contains(searchQuery, ignoreCase = true) ||
                (item.doctorSpecialty?.contains(searchQuery, ignoreCase = true) ?: false)
        matchesTab && matchesStatus && matchesQuery
    }.let { filtered ->
        if (isDescendingSort) {
            filtered.sortedWith(compareByDescending<PatientScreenAppointmentItem> { it.appointmentDate }.thenByDescending { it.appointmentTime })
        } else {
            filtered.sortedWith(compareBy<PatientScreenAppointmentItem> { it.appointmentDate }.thenBy { it.appointmentTime })
        }
    }
}
