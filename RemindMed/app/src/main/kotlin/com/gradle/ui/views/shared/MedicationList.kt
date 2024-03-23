package com.gradle.ui.views.shared

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import com.gradle.constants.GlobalObjects
import com.gradle.apiCalls.Patient as PatientApi
import com.gradle.constants.NavArguments
import com.gradle.constants.Routes
import com.gradle.models.LoginModel
import com.gradle.models.Medication
import com.gradle.ui.components.ButtonSecondary
import com.gradle.ui.components.HeadlineLarge
import com.gradle.ui.components.TitleLarge
import com.gradle.ui.theme.*
import com.gradle.utilities.toFormattedDateString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Time
import java.time.format.DateTimeFormatter

import com.gradle.apiCalls.Medication as MedicationApi

//@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MedicationListScreen(navController: NavController, pid: String) {
    // TODO: Implement some sort async code rot run this on load, and display a loading screen in the meantime
    val coroutineScope = rememberCoroutineScope()
    var patient = PatientApi().getPatientbyId(pid)
    var medications: List<Medication> = PatientApi().getMedicines(pid)

    AppTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.MEDICATION_ENTRY)},
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Medication")
                }
            },
        ) { padding ->
            Column (
                Modifier
                    .padding(padding)
            ) {

                TitleLarge("${patient.name.substringBefore(" ")}'s Medication")

                HeadlineLarge("Medications")
                LazyColumn {
                    items(medications) { medication ->
                        MedicationItem(
                            medication = medication,
                            navController = navController,
//                            onRemove = {
//                                coroutineScope.launch {
//                                    val success = PatientApi().removeMedication(GlobalObjects.patient.pid, medication.medicationId)
//                                    if (success) {
//                                        // Fetch the updated medications list
//                                        medications = PatientApi().getMedicines(GlobalObjects.patient.pid).toList()
//                                    } else {
//                                        // Handle the case where medication removal was unsuccessful
//                                        // This might involve showing an error message to the user
//                                    }
//                                }
//                            },
                            onRemove = {
                                val success = PatientApi().removeMedication(GlobalObjects.patient.pid, medication.medicationId)
                                if (success) { println("Medication Removed")
                                   medications = PatientApi().getMedicines(GlobalObjects.patient.pid).toList()
                                } else {
                                    medications = PatientApi().getMedicines(GlobalObjects.patient.pid).toList()
                                    println("Medication Not Removed")
                                }
                                println(medications)
                            },
                            onClick = {
                                navController.navigate(
                                    Routes.MEDICATION_INFO + "?${NavArguments.MEDICATION_INFO.MEDICATION_NAME}=${medication.name}&" +
                                            "${NavArguments.MEDICATION_INFO.START_DATE}=${medication.startDate}&" +
                                            "${NavArguments.MEDICATION_INFO.END_DATE}=${medication.endDate}&" +
                                            "${NavArguments.MEDICATION_INFO.DOSAGE}=${medication.amount}"
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = (Modifier.height(16.dp)))
                Row (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    ButtonSecondary(text = "See More", onClick = {}, enabled = true)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationItem(
    medication: Medication,
    navController: NavController,
    onRemove: () -> Unit,
    onClick: () -> Unit // Click listener for the entire item
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(6.dp)
            .clickable(onClick = onClick), // Apply click listener to the entire item
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formatter = DateTimeFormatter.ofPattern("MMMM dd")
            val formattedStartDate = medication.startDate.toFormattedDateString().format(formatter)
            val formattedEndDate = medication.endDate.toFormattedDateString().format(formatter)

            Icon(Icons.Outlined.Info, contentDescription = null, Modifier.size(50.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(medication.name, fontWeight = FontWeight.Bold)
                Text(medication.amount, style = MaterialTheme.typography.bodyMedium)
                Text("${medication.startDate} - ${medication.endDate}", style = MaterialTheme.typography.bodyMedium)
                Text("${medication.times}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.weight(1f))

            Column {
                IconButton(onClick = { navController.navigate(Routes.MEDICATION_EDIT) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                // Remove icon
                IconButton(onClick = { showDialog = true }) { // Set showDialog to true when remove icon clicked
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this medication?") },
            confirmButton = {
                Button(
                    onClick = {
                        onRemove()
                        showDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun MedicationListScreenPreview() {
//    MedicationListScreen()
//}
//