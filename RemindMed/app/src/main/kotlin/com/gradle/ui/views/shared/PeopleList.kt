package com.gradle.ui.views.shared

import android.annotation.SuppressLint
import android.os.Handler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gradle.apiCalls.DoctorApi
import com.gradle.apiCalls.PatientApi
import com.gradle.constants.GlobalObjects
import com.gradle.controller.PeopleListController
import com.gradle.models.PeopleList
import com.gradle.ui.components.DoctorItem
import com.gradle.ui.components.LoadingScreen
import com.gradle.ui.components.PeopleListPatientItem
import com.gradle.ui.components.TitleLarge
import com.gradle.ui.theme.AppTheme
import com.gradle.ui.viewModels.LoginViewModel
import com.gradle.ui.viewModels.PeopleListViewModel

enum class PeopleListEvent {
    DeleteEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PeopleListScreen(onNavigateToMedicationList: (String) -> Unit, LoginModel: LoginViewModel) {
    val model: PeopleList = PeopleList()
    val viewModel: PeopleListViewModel by remember { mutableStateOf(PeopleListViewModel(model)) }
    val controller: PeopleListController by remember { mutableStateOf(PeopleListController(model)) }
    var isLoading by remember { mutableStateOf(true) }

    var doctorName by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (GlobalObjects.type == "doctor") {
            viewModel.patientList.value = DoctorApi().getPatients(GlobalObjects.doctor.did)
            doctorName = DoctorApi().getDoctor(GlobalObjects.doctor.did).name
        } else {
            viewModel.doctorList.value = PatientApi().getDoctors(GlobalObjects.patient.pid)
            patientName = PatientApi().getPatientbyId(GlobalObjects.patient.pid).name
        }
        isLoading = false
    }

    AppTheme {
        if (isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(modifier = Modifier.padding()) {
                if (GlobalObjects.type == "doctor") {
                    item {
                        TitleLarge("${doctorName.substringBefore(" ")}'s Patients")
                        HorizontalDivider()
                    }
                    if (viewModel.patientList.value.isEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "No Patients Found",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentHeight(),
                                style = typography.h6,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    items(viewModel.patientList.value) { patient ->
                        PeopleListPatientItem(
                            patient,
                            onNavigateToMedicationList,
                            true,
                            false,
                            { str ->
                                controller.invoke(
                                    PeopleListEvent.DeleteEvent,
                                    str
                                )
                            },
                            LoginModel
                        )
                    }
                } else {
                    if (GlobalObjects.type == "patient") {
                        item {
                            TitleLarge("${patientName.substringBefore(" ")}'s Doctors")
                            HorizontalDivider()
                        }
                    }
                    if (viewModel.doctorList.value.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No Doctors Found",
                                    style = typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    items(viewModel.doctorList.value) { doctor ->
                        DoctorItem(doctor)
                    }
                }
            }

            if (viewModel.showDialog.value && viewModel.successfullyRemovedPatient.value) {
                AlertDialog(
                    onDismissRequest = { viewModel.showDialog.value = false },
                    text = { Text("Success!") },
                    confirmButton = {
                        Button(onClick = { viewModel.showDialog.value = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
                Handler().postDelayed({ viewModel.showDialog.value = false }, 5000)
            } else if (viewModel.showDialog.value && !viewModel.successfullyRemovedPatient.value) {
                AlertDialog(
                    onDismissRequest = { viewModel.showDialog.value = false },
                    text = { Text("Success!") },
                    confirmButton = {
                        Button(onClick = { viewModel.showDialog.value = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
                Handler().postDelayed({ viewModel.showDialog.value = false }, 5000)
            }
        }
    }
}
