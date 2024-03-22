package com.gradle.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gradle.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(state: TimePickerState) {
    var time by remember {
        mutableStateOf("Select Time")
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    AppTheme {
        Box(contentAlignment = Alignment.Center) {
            OutlinedButton(
                onClick = { openDialog = true },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiary
                ),
                enabled = true
            ) {
                Text(text = time)
            }
        }
    }

    if (openDialog) {
        MyTimePickerDialog(
            state = state,
            onDismiss = { openDialog = false },
            onConfirm = { time = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(state: TimePickerState, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    AppTheme {
        Dialog(onDismissRequest = onDismiss) {
            Surface (
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
//                    .padding(10.dp)
            ) {
                Column (modifier = Modifier.padding(30.dp)) {
                    TimePicker(state = state)
                    Row (modifier = Modifier
                        .height(45.dp)
                        .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        ButtonPrimary(text = "Cancel", onClick = { onDismiss() }, enabled = true)
                        ButtonPrimary(
                            text = "Ok",
                            onClick = {
                                val formattedHour = if (state.hour == 0 || state.hour == 12) "12" else String.format("%02d", state.hour % 12)
                                val paddedMinute = String.format("%02d", state.minute)
                                val period = if (state.hour < 12) "AM" else "PM"
                                onConfirm("$formattedHour:$paddedMinute $period")
                                onDismiss()
                            }, enabled = true)
                    }
                }
            }


        }
    }
}