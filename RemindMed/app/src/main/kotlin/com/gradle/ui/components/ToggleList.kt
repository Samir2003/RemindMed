package com.gradle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gradle.ui.theme.AppTheme

@Composable
fun ToggleList(header: String, content: String, open: Boolean = false) {
    val isExpanded = remember { mutableStateOf(open) }

    AppTheme {
        Column {
            Row(
                modifier = Modifier
                    .clickable { isExpanded.value = !isExpanded.value }
            ) {
                if (isExpanded.value) {
                    HeadLineMedium(text = "▼ $header")
                } else {
                    HeadLineMedium(text = "▶ $header")
                }
            }

            if (isExpanded.value) {
                Text(text = content)
            }
        }

    }
}