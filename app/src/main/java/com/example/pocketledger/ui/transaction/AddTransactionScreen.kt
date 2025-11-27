package com.example.pocketledger.ui.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketledger.data.model.TransactionDirection
import com.example.pocketledger.ui.common.AccountSelector
import com.example.pocketledger.ui.common.AmountInput
import com.example.pocketledger.ui.common.DateTimePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Direction Chips
            Row(modifier = Modifier.fillMaxWidth()) {
                TransactionDirection.values().forEach { direction ->
                    FilterChip(
                        selected = uiState.direction == direction,
                        onClick = { viewModel.onDirectionChange(direction) },
                        label = { Text(direction.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AmountInput(
                amount = uiState.amount,
                onAmountChange = viewModel::onAmountChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateTimePicker(
                dateTime = uiState.datetime,
                onDateTimeSelected = viewModel::onDateTimeChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            AccountSelector(
                accounts = accounts,
                selectedAccount = uiState.fromAccount,
                onAccountSelected = viewModel::onFromAccountChange,
                label = if (uiState.direction == TransactionDirection.INCOME) "To Account" else "From Account"
            )

            if (uiState.direction == TransactionDirection.TRANSFER) {
                Spacer(modifier = Modifier.height(16.dp))
                AccountSelector(
                    accounts = accounts,
                    selectedAccount = uiState.toAccount,
                    onAccountSelected = viewModel::onToAccountChange,
                    label = "To Account"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.category,
                onValueChange = viewModel::onCategoryChange,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveTransaction(onSuccess = onBackClick) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.amount.isNotEmpty() && uiState.fromAccount != null && uiState.category.isNotEmpty()
            ) {
                Text("Save Transaction")
            }
        }
    }
}
