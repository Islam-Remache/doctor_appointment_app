package com.example.learning.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learning.R // Assuming your R file is in com.example.learning
import com.example.learning.model.Doctor
import com.example.learning.ui.theme.StarYellow
import com.example.learning.ui.theme.TextMutedGray

@Composable
fun DoctorProfileCard(
    doctor: Doctor,
    specialtyName: String,
    onDoctorClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onDoctorClick(doctor.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder bg
            ) {
                // TODO: Replace with Coil/Glide for network image loading for doctor.photo_url
                Image(
                    painter = painterResource(id = R.drawable.ic_doctor_placeholder), // Ensure this drawable exists
                    contentDescription = "Doctor ${doctor.fullName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 8.dp), // Space before favorite icon
                verticalArrangement = Arrangement.SpaceAround // Distribute elements vertically
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = doctor.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(1f, fill = false), // Prevents pushing icon off
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Favorite Icon (can be made interactive later)
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = TextMutedGray,
                        modifier = Modifier.padding(start = 8.dp) // Space from text
                    )
                }

                Text(
                    text = specialtyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMutedGray
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = StarYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.5", // Example rating - this should come from Doctor model if available
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("|", color = TextMutedGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100 Reviews", // Example reviews - from Doctor model if available
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedGray
                    )
                }
            }
        }
    }
}