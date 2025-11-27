package com.example.pocketledger.ui.common

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimePicker(
    dateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    val showPickerDialog = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        onDateTimeSelected(LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute))
                    },
                    dateTime.hour,
                    dateTime.minute,
                    false
                ).show()
            },
            dateTime.year,
            dateTime.monthValue - 1,
            dateTime.dayOfMonth
        ).show()
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = dateTime.format(formatter),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date & Time") },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false // Disable direct editing, handle click on Box or use interaction source
        )
        // Overlay to capture clicks
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPickerDialog() }
        )
    }
}
