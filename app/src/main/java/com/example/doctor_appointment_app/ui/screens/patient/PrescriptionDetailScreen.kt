package com.example.prescription_manag2.ui.screens.patient

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.prescription_manag2.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.prescription_manag2.models.PrescriptionModel
import com.example.prescription_manag2.viewmodel.PrescriptionViewModel
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrescriptionDetailScreen(
    prescriptionId: Long,
    navController: NavController,
    context: Context,
    viewModel: PrescriptionViewModel
) {
    val localContext = LocalContext.current

    // State to manage loading dialog display
    var showLoading by remember { mutableStateOf(false) }

    // Get patient from viewModel
    val patients = viewModel.patients.value
    val patient = patients.firstOrNull()

    // Load prescriptions for the patient
    LaunchedEffect(patient) {
        patient?.let {
            viewModel.getCompletePrescriptionDataRoom(it.id)
        }
    }

    // Get prescriptions from viewModel state
    val prescriptions by remember { viewModel.prescriptions }

    // Find the prescription with matching ID
    val prescription = prescriptions.find { it.id == prescriptionId }

    // State for permissions
    var hasRequiredPermissions by remember { mutableStateOf(false) }

    // Check permissions at startup
    LaunchedEffect(Unit) {
        hasRequiredPermissions = checkWritePermission(localContext)
    }

    // Launcher for permission requests
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRequiredPermissions = isGranted
        if (isGranted && prescription != null) {
            showLoading = true
            // Generate PDF in a separate thread
            Thread {
                val pdfFile = savePrescriptionAsPdf(prescription, localContext)
                // Update UI in main thread
                (localContext as? android.app.Activity)?.runOnUiThread {
                    showLoading = false
                    pdfFile?.let { sharePdf(localContext, it) }
                }
            }.start()
        } else if (!isGranted) {
            Toast.makeText(localContext, "Permission needed to save PDF", Toast.LENGTH_LONG).show()
        }
    }

    if (prescription == null) {
        Text(text = "", modifier = Modifier.padding(16.dp))
        return
    }

    // Show loading dialog during PDF generation
    if (showLoading) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Generating PDF") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Please wait...")
                }
            },
            confirmButton = { }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Title Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 45.dp, start = 2.dp, end = 2.dp, bottom = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Prescription Details",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color(0xFF0D6783)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(25.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prescription",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D6783),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Created:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0D6783)
                            )
                            Text(
                                text = if (prescription.createdAt.length >= 10)
                                    prescription.createdAt.substring(0, 10)
                                else
                                    prescription.createdAt,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Expires:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0D6783)
                            )
                            Text(
                                text = if (prescription.expiresAt.length >= 10)
                                    prescription.expiresAt.substring(0, 10)
                                else
                                    prescription.expiresAt,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Doctor's image
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.doctor1),
                            contentDescription = "Doctor Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Doctor's details
                    Column {
                        Text(
                            text = "${prescription.doctor.firstName} ${prescription.doctor.lastName}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prescription.doctor.specialty?.label ?: "Specialist",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7BC1B7)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(15.dp))

                // Clinic Info
                Text(text = "Clinic Info", fontWeight = FontWeight.Medium, fontSize = 19.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Clinic name: ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D6783)
                    )
                    Text(
                        text = prescription.doctor.healthInstitution?.name ?: "N/A",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Clinic address: ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D6783)
                    )
                    Text(
                        text = prescription.doctor.healthInstitution?.address ?: "N/A",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Patient Info", fontWeight = FontWeight.Medium, fontSize = 19.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Patient name: ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D6783)
                    )
                    Text(
                        text = "${prescription.patient.firstName} ${prescription.patient.lastName}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Age: ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D6783)
                    )
                    Text(
                        text = "${prescription.patient.age}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Divider(
                    color = Color(0xFF7BC1B7),
                    thickness = 2.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.4f)
                )

                Text(text = "Medication Info", fontWeight = FontWeight.Medium, fontSize = 19.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Medication names: ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D6783)
                    )
                    Text(
                        text = prescription.medications.joinToString(", ") { it.name ?: "" },
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Dosage & Frequency:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D6783)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    prescription.medications.forEach { medication ->
                        Text(
                            text = "- ${medication.name ?: ""}, ${medication.dosage ?: ""}, ${medication.frequency ?: ""}, ${medication.duration ?: ""}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Instructions:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0D6783),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Text(
                    text = prescription.instructions,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Download Button
        Button(
            onClick = {
                if (hasRequiredPermissions) {
                    showLoading = true
                    // Generate PDF in a separate thread
                    Thread {
                        val pdfFile = savePrescriptionAsPdf(prescription, localContext)
                        // Update UI in main thread
                        (localContext as? android.app.Activity)?.runOnUiThread {
                            showLoading = false
                            pdfFile?.let { sharePdf(localContext, it) }
                        }
                    }.start()
                } else {
                    // Request permissions
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF0D6783)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Open PDF", color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.download),
                contentDescription = "Download Icon",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Check if the application has external write permission
 */
fun checkWritePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+ no longer needs STORAGE permission to write to Downloads
        true
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Generates a PDF from prescription data
 */
@RequiresApi(Build.VERSION_CODES.O)
fun savePrescriptionAsPdf(prescription: PrescriptionModel, context: Context): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    try {
        // Creating PDF with improved layout
        createPrescriptionPdf(canvas, prescription)

        pdfDocument.finishPage(page)

        // Create file in download directory
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "prescription_${prescription.id}_$timestamp.pdf"

        val pdfFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - use temporary file in app cache
            File(context.cacheDir, fileName)
        } else {
            // Earlier versions - use public download directory
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        }

        FileOutputStream(pdfFile).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }

        // Show success message
        (context as? android.app.Activity)?.runOnUiThread {
            Toast.makeText(context, "Opening PDF preview...", Toast.LENGTH_SHORT).show()
        }

        return pdfFile

    } catch (e: IOException) {
        e.printStackTrace()
        (context as? android.app.Activity)?.runOnUiThread {
            Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
        return null
    } finally {
        pdfDocument.close()
    }
}

/**
 * Creates PDF content with nice layout
 */
private fun createPrescriptionPdf(canvas: Canvas, prescription: PrescriptionModel) {
    val width = canvas.width.toFloat()

    // Text style configuration
    val titlePaint = Paint().apply {
        textSize = 27f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        color = android.graphics.Color.rgb(13, 103, 131) // 0xFF0D6783
    }

    val subtitlePaint = Paint().apply {
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        color = android.graphics.Color.rgb(13, 103, 131) // 0xFF0D6783
    }

    val headerPaint = Paint().apply {
        color = 0xFF000000.toInt()
        isFakeBoldText = true
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }

    val labelPaint = Paint().apply {
        textSize = 15f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        color = android.graphics.Color.rgb(13, 103, 131) // 0xFF0D6783
    }

    val textPaint = Paint().apply {
        textSize = 15f
        color = android.graphics.Color.BLACK
    }

    val linePaint = Paint().apply {
        color = android.graphics.Color.LTGRAY
        strokeWidth = 1f
    }

    // Main title
    var yPos = 60f
    val title = "MEDICAL PRESCRIPTION"
    canvas.drawText(title, (width - titlePaint.measureText(title)) / 2, yPos, titlePaint)

    // Separator line
    yPos += 20f
    canvas.drawLine(40f, yPos, width - 40, yPos, linePaint)

    // Doctor information
    yPos += 50f  // Increased spacing
    canvas.drawText("Doctor", 40f, yPos, subtitlePaint)

    yPos += 30f  // Increased spacing
    canvas.drawText("Dr. ${prescription.doctor.firstName} ${prescription.doctor.lastName}", 60f, yPos, headerPaint)

    yPos += 22f
    canvas.drawText("Specialty: ${prescription.doctor.specialty?.label ?: "General Practitioner"}", 60f, yPos, textPaint)

    yPos += 22f
    canvas.drawText("Institution: ${prescription.doctor.healthInstitution?.name ?: "N/A"}", 60f, yPos, textPaint)

    yPos += 22f
    canvas.drawText("Address: ${prescription.doctor.healthInstitution?.address ?: "N/A"}", 60f, yPos, textPaint)

    // Separator line
    yPos += 35f  // Increased spacing
    canvas.drawLine(40f, yPos, width - 40, yPos, linePaint)

    // Patient information
    yPos += 35f  // Increased spacing
    canvas.drawText("Patient", 40f, yPos, subtitlePaint)

    yPos += 30f  // Increased spacing
    canvas.drawText("Name: ${prescription.patient.firstName} ${prescription.patient.lastName}", 60f, yPos, textPaint)

    yPos += 22f
    canvas.drawText("Age: ${prescription.patient.age}", 60f, yPos, textPaint)

    // Separator line
    yPos += 35f  // Increased spacing
    canvas.drawLine(40f, yPos, width - 40, yPos, linePaint)

    // Prescription information
    yPos += 35f  // Increased spacing
    canvas.drawText("Prescription Details", 40f, yPos, subtitlePaint)

    yPos += 30f  // Increased spacing
    canvas.drawText("Created on: ${prescription.createdAt?.take(10) ?: "N/A"}", 60f, yPos, textPaint)

    yPos += 22f
    canvas.drawText("Expires on: ${prescription.expiresAt?.take(10) ?: "N/A"}", 60f, yPos, textPaint)

    // Separator line
    yPos += 35f  // Increased spacing
    canvas.drawLine(40f, yPos, width - 40, yPos, linePaint)

    yPos += 35f
    canvas.drawText("Medication Names:", 40f, yPos, subtitlePaint)
    yPos += 30f

    val medicationNames = prescription.medications.joinToString(", ") { it.name ?: "N/A" }
    canvas.drawText(medicationNames, 60f, yPos, textPaint)

    yPos += 40f
    canvas.drawText("Dosage & Frequency:", 40f, yPos, subtitlePaint)

    for (medication in prescription.medications) {
        yPos += 25f
        val dosageInfo = "${medication.dosage ?: "N/A"}, ${medication.frequency ?: "N/A"}, ${medication.duration ?: "N/A"}"
        canvas.drawText(dosageInfo, 60f, yPos, textPaint)
    }

    // Instructions
    yPos += 35f  // Increased spacing
    canvas.drawText("Instructions", 40f, yPos, subtitlePaint)

    yPos += 30f  // Increased spacing

    // Draw instructions text with line breaks if needed
    val instructionsText = prescription.instructions
    val maxWidth = width - 100f

    // Split text into words
    val words = instructionsText.split(" ")
    var currentLine = ""

    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        val testWidth = textPaint.measureText(testLine)

        if (testWidth > maxWidth) {
            // Draw current line and move to next line
            canvas.drawText(currentLine, 60f, yPos, textPaint)
            yPos += 22f
            currentLine = word
        } else {
            currentLine = testLine
        }
    }

    // Draw last line
    if (currentLine.isNotEmpty()) {
        canvas.drawText(currentLine, 60f, yPos, textPaint)
    }

    // Footer
    yPos = 800f
    val footer = "Document generated on ${SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault()).format(Date())}"
    canvas.drawText(footer, (width - textPaint.measureText(footer)) / 2, yPos, textPaint)
}

/**
 * Share generated PDF via system intent
 */
fun sharePdf(context: Context, file: File) {
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } else {
        Uri.fromFile(file)
    }

    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Open PDF with..."))
    } catch (e: Exception) {
        Toast.makeText(context, "No application found that can open PDF files", Toast.LENGTH_LONG).show()
    }
}