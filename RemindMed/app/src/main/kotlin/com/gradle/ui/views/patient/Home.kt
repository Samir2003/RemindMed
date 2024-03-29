package com.gradle.ui.views.patient
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
//import com.gradle.models.Medication
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gradle.models.CalendarModel
import com.gradle.utilities.CalendarDataSource
import com.gradle.utilities.toFormattedDateShortString
import com.gradle.utilities.toFormattedDateString
import com.gradle.utilities.toFormattedMonthDateString
import com.example.remindmed.R
import com.gradle.apiCalls.Doctor
import com.gradle.constants.GlobalObjects
import com.gradle.constants.Routes
import com.gradle.controller.AddPatientController
import com.gradle.models.AddPatient
import com.gradle.models.Medication
import com.gradle.models.Patient
import com.gradle.ui.theme.AppTheme
import com.gradle.ui.views.doctor.AddPatientViewModel
import io.ktor.util.date.Month
import kotlinx.coroutines.selects.select
import java.time.LocalDateTime
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.isGranted
//import com.google.accompanist.permissions.rememberPermissionState
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

import com.gradle.apiCalls.Patient as PatientApi


class Prescription (
    val name: String,
    val amount: String,
    val times: String
)

@Composable
fun MedicationItem(
    prescription: Prescription,
    taken: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(15.dp)),

        colors = cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
        ),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(prescription.name, fontWeight = FontWeight.Bold)
                Text("Dosage: ${prescription.amount}", style = MaterialTheme.typography.bodyMedium)

                Text("Time: ${prescription.times}", style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(
                checked = taken,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkmarkColor = Color.White),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun MedicationListHomeScreen(
    takenMedications: List<Prescription>,
    notTakenMedications: List<Prescription>,
    onCheckedChange: (Prescription, Boolean) -> Unit
) {

    Column {
        Text("Taken:", modifier = Modifier.padding(8.dp),style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        takenMedications.forEach { medication ->
            MedicationItem(prescription = medication, taken = true, onCheckedChange = { isChecked ->
                onCheckedChange(medication, isChecked)
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Not Finished:", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        notTakenMedications.forEach { medication ->
            MedicationItem(prescription = medication, taken = false, onCheckedChange = { isChecked ->
                onCheckedChange(medication, isChecked)
            })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    var takenMedications by remember { mutableStateOf(listOf<Prescription>()) }
    var notTakenMedications by remember { mutableStateOf(listOf<Prescription>()) }
    val today = Calendar.getInstance().time
    var selectedDate by remember { mutableStateOf(today) }

    var medications by remember { mutableStateOf(emptyList<Medication>()) }
    var patient by remember { mutableStateOf(Patient("")) }
    var greeting by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        patient = PatientApi().getPatientbyId(GlobalObjects.patient.pid)
        medications = PatientApi().getMedicines(GlobalObjects.patient.pid)
        greeting = when (LocalDateTime.now().hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    var medicationsToday = medications.filter { medication ->
        (selectedDate >= medication.startDate && selectedDate < Date(medication.endDate.time + TimeUnit.DAYS.toMillis(1)))
    }
    notTakenMedications = medicationsToday.map { medication ->
        Prescription(medication.name, medication.amount, medication.times.toString())
    }

    // some weird issue here with slight UI lag, come back to this
//    takenMedications = emptyList() // Clear the taken medications list
//    LaunchedEffect(selectedDate) {
//        println(selectedDate)
//        medications = PatientApi().getMedicines(GlobalObjects.patient.pid)
//        medicationsToday = medications.filter { medication ->
//            (selectedDate >= medication.startDate  && selectedDate <= medication.endDate)
//        }
//        notTakenMedications = medicationsToday.map { medication ->
//            Prescription(medication.name, medication.amount, medication.times.toString())
//        }
//        takenMedications = emptyList() // Clear the taken medications list
//    }

    takenMedications = emptyList()
    notTakenMedications = medicationsToday.map { medication ->
        Prescription(medication.name, medication.amount, medication.times.toString())
    }

    AppTheme {
        Scaffold(
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {

                DatesHeader(
                    onDateSelected = { newSelectedDate ->
                        selectedDate = newSelectedDate // need to restrict it so that medications for days other than today cannot be marked taken,
                        // user can only "take" medications for the current date, selecting a new date just allows for user to see what medications they have for that day
                    }
                )
                DailyOverviewCard(medicationsToday = emptyList(), logEvent = {}, greeting, patient)
                Spacer(modifier = Modifier.height(16.dp))

                MedicationListHomeScreen(
                    takenMedications = takenMedications,
                    notTakenMedications = notTakenMedications
                ) { medication, isChecked ->
                    if (isChecked) {
                        // Move medication from not taken to taken
                        notTakenMedications = notTakenMedications.filterNot { it == medication }
                        takenMedications = takenMedications + medication
                    } else {
                        // Move medication from taken to not taken
                        takenMedications = takenMedications.filterNot { it == medication }
                        notTakenMedications = notTakenMedications + medication
                    }
                }
            }
        }
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//@Preview
//@Composable
//fun PreviewHomeScreen() {
//    HomeScreen(navController = rememberNavController())
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyOverviewCard(
    medicationsToday: List<Medication>,
    logEvent: (String) -> Unit,
    greeting: String,
    patient: Patient
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .height(156.dp),
        shape = RoundedCornerShape(15.dp),
        colors = cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.White
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp, 24.dp, 0.dp, 16.dp)
                    .fillMaxWidth(.50F)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${greeting} ${patient.name.substringBefore(" ")},",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Check off the medications that you have taken today ",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    painter = painterResource(id = R.drawable.doctor), contentDescription = ""
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyOverviewEmptyCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp),
        shape = RoundedCornerShape(36.dp),
        colors = cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = {
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(24.dp, 24.dp, 0.dp, 16.dp)
                    .fillMaxWidth(.50F)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                Text(
                    text = "Medication Break",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = "No medications scheduled for this date. Take a break and relax.",
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    painter = painterResource(id = R.drawable.doctor), contentDescription = ""
                )
            }
        }
    }
}

val monthNames = mapOf(
    1 to "Jan",
    2 to "Feb",
    3 to "Mar",
    4 to "Apr",
    5 to "May",
    6 to "Jun",
    7 to "Jul",
    8 to "Aug",
    9 to "Sep",
    10 to "Oct",
    11 to "Nov",
    12 to "Dec"
)

@Composable
fun DatesHeader(
    onDateSelected: (Date) -> Unit
) {
    val dataSource = CalendarDataSource()
    var calendarModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        DateHeader(
            data = calendarModel,
            onPrevClickListener = { startDate ->
                val calendar = Calendar.getInstance()
                calendar.time = startDate
                calendar.add(Calendar.DAY_OF_YEAR, -2)
                val finalStartDate = calendar.time
                calendarModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarModel.selectedDate.date)
            },
            onNextClickListener = { endDate ->
                val calendar = Calendar.getInstance()
                calendar.time = endDate
                calendar.add(Calendar.DAY_OF_YEAR, 2)
                val finalStartDate = calendar.time
                calendarModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarModel.selectedDate.date)
            }
        )
        DateList(
            data = calendarModel,
            onDateClickListener = { dateModel ->
                calendarModel = calendarModel.copy(
                    selectedDate = dateModel,
                    visibleDates = calendarModel.visibleDates.map { visibleDate ->
                        visibleDate.copy(
                            isSelected = visibleDate.date.toFormattedDateString() == dateModel.date.toFormattedDateString()
                        )
                    }
                )
                onDateSelected(dateModel.date)
            }
        )
    }
}

@Composable
fun DateList(
    data: CalendarModel,
    onDateClickListener: (CalendarModel.DateModel) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(items = data.visibleDates) { date ->
            DateItem(date, onDateClickListener)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateItem(
    date: CalendarModel.DateModel,
    onClickListener: (CalendarModel.DateModel) -> Unit,
) {
    Column {
        Text(
            text = date.day, // day "Mon", "Tue"
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Card(
            modifier = Modifier
                .padding(horizontal = 2.dp),
            onClick = { onClickListener(date) },
            colors = cardColors(
                // background colors of the selected date
                // and the non-selected date are different
                containerColor = if (date.isSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
        ) {
            Column(
                modifier = Modifier
                    .width(42.dp)
                    .height(42.dp)
                    .padding(2.dp)
                    .fillMaxSize(), // Fill the available size in the Column
                verticalArrangement = Arrangement.Center, // Center vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
            ) {
                Text(
                    text = date.date.toFormattedDateShortString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (date.isSelected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Bold
                    }
                )
            }

        }
        Text(
            text = monthNames[date.date.month.plus(1)] ?: "",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (date.isSelected) {
                FontWeight.Bold
            } else {
                FontWeight.Bold
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
fun DateHeader(
    data: CalendarModel,
    onPrevClickListener: (Date) -> Unit,
    onNextClickListener: (Date) -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.toFormattedMonthDateString()
            },
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = {
            onPrevClickListener(data.startDate.date)
        }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Back"
            )
        }
        IconButton(onClick = {
            onNextClickListener(data.endDate.date)
        }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Next"
            )
        }
    }
}

//sealed class MedicationListItem {
//    data class OverviewItem(val medicationsToday: List<Medication>, val isMedicationListEmpty: Boolean) : MedicationListItem()
//    data class MedicationItem(val medication: Medication) : MedicationListItem()
//    data class HeaderItem(val headerText: String) : MedicationListItem()
//}

//@Composable
//fun DailyMedications(
//    navController: NavController,
//    state: HomeState,
//    navigateToMedicationDetail: (Medication) -> Unit,
//    logEvent: (String) -> Unit
//) {
//
//    var filteredMedications: List<Medication> by remember { mutableStateOf(emptyList()) }
//
//    DatesHeader(
//        onDateSelected = { selectedDate ->
////            val newMedicationList = state.medications
////                .filter { medication ->
////                    medication.medicationTime.toFormattedDateString() == selectedDate.date.toFormattedDateString()
////                }
////                .sortedBy { it.medicationTime }
//
////            filteredMedications = newMedicationList
//        }
//    )
//
//    if (filteredMedications.isEmpty()) {
//        EmptyCard(
//            navController = navController,
//        )
//    } else {
//        LazyColumn(
//            modifier = Modifier,
//        ) {
////            items(
////                items = filteredMedications,
////                itemContent = {
////                    MedicationSummaryCard(
////                        medication = it,
////                        navigateToMedicationDetail = { medication ->
////                            navigateToMedicationDetail(medication)
////                        }
////                    )
////                }
////            )
//        }
//    }
//}

//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun PermissionDialog(
//    askNotificationPermission: Boolean,
//    logEvent: (String) -> Unit
//) {
//    if (askNotificationPermission && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
//        val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
//            when (isGranted) {
//                true -> logEvent.invoke(AnalyticsEvents.NOTIFICATION_PERMISSION_GRANTED)
//                false -> logEvent.invoke(AnalyticsEvents.NOTIFICATION_PERMISSION_REFUSED)
//            }
//        }
//        if (!notificationPermissionState.status.isGranted) {
//            val openAlertDialog = remember { mutableStateOf(true) }
//
//            when {
//                openAlertDialog.value -> {
//                    logEvent.invoke(AnalyticsEvents.NOTIFICATION_PERMISSION_DIALOG_SHOWN)
//                    AlertDialog(
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Default.Notifications,
//                                contentDescription = stringResource(R.string.notifications)
//                            )
//                        },
//                        title = {
//                            Text(text = stringResource(R.string.notification_permission_required))
//                        },
//                        text = {
//                            Text(text = stringResource(R.string.notification_permission_required_description_message))
//                        },
//                        onDismissRequest = {
//                            openAlertDialog.value = false
//                            logEvent.invoke(AnalyticsEvents.NOTIFICATION_PERMISSION_DIALOG_DISMISSED)
//                        },
//                        confirmButton = {
//                            Button(
//                                onClick = {
//                                    notificationPermissionState.launchPermissionRequest()
//                                    openAlertDialog.value = false
//                                    logEvent.invoke(AnalyticsEvents.NOTIFICATION_PERMISSION_DIALOG_ALLOW_CLICKED)
//                                }
//                            ) {
//                                Text(stringResource(R.string.allow))
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

// @OptIn(ExperimentalPermissionsApi::class)
// @Composable
// fun PermissionAlarmDialog(
//     askAlarmPermission: Boolean,
//     logEvent: (String) -> Unit
// ) {
//     val context = LocalContext.current
//     val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
//     if (askAlarmPermission && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
//         val alarmPermissionState = rememberPermissionState(Manifest.permission.SCHEDULE_EXACT_ALARM) { isGranted ->
//             when (isGranted) {
//                 true -> logEvent.invoke(AnalyticsEvents.ALARM_PERMISSION_GRANTED)
//                 false -> logEvent.invoke(AnalyticsEvents.ALARM_PERMISSION_REFUSED)
//             }
//         }
//         if (alarmManager?.canScheduleExactAlarms() == false) {
//             val openAlertDialog = remember { mutableStateOf(true) }

//             when {
//                 openAlertDialog.value -> {

//                     logEvent.invoke(AnalyticsEvents.ALARM_PERMISSION_DIALOG_SHOWN)

//                     AlertDialog(
//                         icon = {
//                             Icon(
//                                 imageVector = Icons.Default.Notifications,
//                                 contentDescription = stringResource(R.string.alarms)
//                             )
//                         },
//                         title = {
//                             Text(text = stringResource(R.string.alarms_permission_required))
//                         },
//                         text = {
//                             Text(text = stringResource(R.string.alarms_permission_required_description_message))
//                         },
//                         onDismissRequest = {
//                             openAlertDialog.value = false
//                             logEvent.invoke(AnalyticsEvents.ALARM_PERMISSION_DIALOG_DISMISSED)
//                         },
//                         confirmButton = {
//                             Button(
//                                 onClick = {
//                                     Intent().also { intent ->
//                                         intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
//                                         context.startActivity(intent)
//                                     }

//                                     openAlertDialog.value = false
//                                     logEvent.invoke(AnalyticsEvents.ALARM_PERMISSION_DIALOG_ALLOW_CLICKED)
//                                 }
//                             ) {
//                                 Text(stringResource(R.string.allow))
//                             }
//                         }
//                     )
//                 }
//             }
//         }
//     }
// }
