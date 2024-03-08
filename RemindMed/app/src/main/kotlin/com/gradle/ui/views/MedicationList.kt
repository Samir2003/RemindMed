package com.gradle.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gradle.constants.Routes
import com.gradle.ui.theme.*

data class Medication(val name: String, val dosage: String, val time: String, val instructions: String)
// have to integrate with the medication model

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MedicationListScreen(navController: NavController) {
    // Just some starter medications here for now as a view example
    // --> to do: need to integrate the logic and pull from database
    val doctorAssignedMedications = listOf(
        Medication("Reserphine", "3 ml per usage", "9:00 AM", "After Breakfast"),
    )

    val selfAssignedMedications = listOf(
        Medication("Vitamin D", "4g twice per day", "9:00 AM + 12:00 AM", "After Breakfast + Lunch"),
    )

    AppTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.USER_MEDICATION_ENTRY)},
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
                TitleLarge("Medication")

                HeadlineLarge("Doctor Assigned")
                LazyColumn {
                    items(doctorAssignedMedications) { medication ->
                        MedicationItem(medication)
                    }
                }
                Row (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    ButtonSecondary(text = "See more", onClick = {}, enabled = true)
                }

                HeadlineLarge("Self Assigned")
                LazyColumn {
                    items(selfAssignedMedications) { medication ->
                        MedicationItem(medication)
                    }
                }
                Row (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    ButtonSecondary(text = "See more", onClick = {}, enabled = true)
                }
            }
        }
    }
}

@Composable
fun MedicationItem(medication: Medication) {
    Card(
        modifier = Modifier.padding(6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),

        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
//            Image (
//                // painter = painterResource(id = R.drawable.ic_medication_placeholder), // Replace with the actual image
//                painter = painterResource(id = 1),
//                contentDescription = medication.name,
//                modifier = Modifier.size(48.dp)
//            )
            Icon(Icons.Outlined.Info, contentDescription = null, Modifier.size(50.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(medication.name, fontWeight = FontWeight.Bold)
                Text("Dosage: ${medication.dosage}")
                Text(medication.time)
                Text(medication.instructions)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO: Handle navigate to medication details */ }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Go to details")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MedicationListScreenPreview() {
//    MedicationListScreen()
//}
//