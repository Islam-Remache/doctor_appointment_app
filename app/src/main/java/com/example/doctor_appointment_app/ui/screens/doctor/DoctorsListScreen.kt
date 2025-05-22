package com.example.learning.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learning.model.Doctor
import com.example.learning.model.SpecialtyResponse
import com.example.learning.ui.components.AppFilterChip
import com.example.learning.ui.components.DoctorProfileCard
import com.example.learning.ui.components.FullScreenError
import com.example.learning.ui.components.FullScreenLoading
import com.example.learning.ui.theme.SearchFieldBackground
import com.example.learning.ui.theme.TextMutedGray
import com.example.learning.viewmodel.DoctorsListViewModel
import com.example.learning.viewmodel.DoctorsListViewModelFactory

@Composable
fun DoctorsListScreen(
    onDoctorClick: (doctorId: Int) -> Unit,
    viewModel: DoctorsListViewModel = viewModel(factory = DoctorsListViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredDoctors by viewModel.filteredDoctors.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSpecialtyId by viewModel.selectedSpecialtyId.collectAsState()

    when {
        uiState.isLoading -> FullScreenLoading()
        uiState.error != null -> FullScreenError(message = uiState.error!!, onRetry = { viewModel.retryLoad() })
        else -> {
            DoctorsListContent(
                doctors = filteredDoctors,
                specialties = uiState.specialties,
                searchQuery = searchQuery,
                selectedSpecialtyId = selectedSpecialtyId,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onSpecialtySelected = viewModel::onSpecialtySelected,
                onDoctorClick = onDoctorClick
            )
        }
    }
}

@Composable
fun DoctorsListContent(
    doctors: List<Doctor>,
    specialties: List<SpecialtyResponse>,
    searchQuery: String,
    selectedSpecialtyId: Int?,
    onSearchQueryChanged: (String) -> Unit,
    onSpecialtySelected: (Int?) -> Unit,
    onDoctorClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "All Doctors",
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Search by Doctor's Name", color = TextMutedGray) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon", tint = TextMutedGray)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search", tint = TextMutedGray)
                    }
                }
            },
            singleLine = true,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Use onSurface for text input
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Reduced padding
                .clip(RoundedCornerShape(12.dp))
                .background(SearchFieldBackground, shape = RoundedCornerShape(12.dp)),
            colors = OutlinedTextFieldDefaults.colors( // Use colors for M3
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), // Lighter border
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = TextMutedGray
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            item {
                AppFilterChip(
                    selected = selectedSpecialtyId == null,
                    onClick = { onSpecialtySelected(null) },
                    label = { Text("All") }
                )
            }
            items(specialties) { spec ->
                AppFilterChip(
                    selected = selectedSpecialtyId == spec.id,
                    onClick = { onSpecialtySelected(spec.id) },
                    label = { Text(spec.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (doctors.isEmpty()) {
            Text(
                "No doctors found matching your criteria.",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp) // Padding at the bottom of the list
            ) {
                items(doctors, key = { it.id }) { doctor ->
                    val specialtyName = specialties.find { it.id == doctor.specialty_id }?.label ?: "General"
                    DoctorProfileCard(
                        doctor = doctor,
                        specialtyName = specialtyName,
                        onDoctorClick = onDoctorClick
                    )
                }
            }
        }
    }
}