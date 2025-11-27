package com.example.pocketledger.ui.screens.add_transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.model.LoanRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle success and navigation back
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveTransaction()
                        },
                        enabled = uiState.amount.isNotBlank() && 
                                uiState.fromAccountId.isNotBlank() && 
                                uiState.category.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Amount Input
                item {
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = viewModel::updateAmount,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Amount*") },
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                }

                // Transaction Direction
                item {
                    Text("Transaction Type*", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Transaction.TransactionDirection.values().forEach { direction ->
                            FilterChip(
                                selected = uiState.direction == direction,
                                onClick = { viewModel.updateDirection(direction) },
                                label = { Text(direction.name) }
                            )
                        }
                    }
                }

                // Account Selection
                item {
                    if (uiState.accounts.isNotEmpty()) {
                        Text("From Account*", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.accounts.forEach { account ->
                                FilterChip(
                                    selected = uiState.fromAccountId == account.id,
                                    onClick = { viewModel.updateFromAccount(account.id) },
                                    label = { Text(account.name) }
                                )
                            }
                        }
                    }
                }

                // To Account (for transfers)
                if (uiState.direction == Transaction.TransactionDirection.TRANSFER) {
                    item {
                        if (uiState.accounts.isNotEmpty()) {
                            Text("To Account*", style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.accounts
                                    .filter { it.id != uiState.fromAccountId }
                                    .forEach { account ->
                                        FilterChip(
                                            selected = uiState.toAccountId == account.id,
                                            onClick = { viewModel.updateToAccount(account.id) },
                                            label = { Text(account.name) }
                                        )
                                    }
                            }
                        }
                    }
                }

                // Category
                item {
                    Column {
                        OutlinedTextField(
                            value = uiState.category,
                            onValueChange = viewModel::updateCategory,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Category*") },
                            singleLine = true
                        )
                        
                        // Recent categories suggestions
                        if (uiState.recentCategories.isNotEmpty()) {
                            Text("Recent categories:", style = MaterialTheme.typography.bodySmall)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                uiState.recentCategories.take(5).forEach { category ->
                                    AssistChip(
                                        onClick = { viewModel.updateCategory(category) },
                                        label = { Text(category) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Counterparty
                item {
                    Column {
                        OutlinedTextField(
                            value = uiState.counterparty,
                            onValueChange = viewModel::updateCounterparty,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Counterparty (Person/Shop)") },
                            singleLine = true
                        )
                        
                        // Recent counterparties suggestions
                        if (uiState.recentCounterparties.isNotEmpty()) {
                            Text("Recent counterparties:", style = MaterialTheme.typography.bodySmall)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                uiState.recentCounterparties.take(5).forEach { counterparty ->
                                    AssistChip(
                                        onClick = { viewModel.updateCounterparty(counterparty) },
                                        label = { Text(counterparty) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Note
                item {
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = viewModel::updateNote,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Note (optional)") },
                        maxLines = 3
                    )
                }

                // Tags
                item {
                    Column {
                        Text("Tags", style = MaterialTheme.typography.titleMedium)
                        
                        // Existing tags
                        if (uiState.tags.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                uiState.tags.forEach { tag ->
                                    InputChip(
                                        selected = false,
                                        onClick = { viewModel.removeTag(tag) },
                                        label = { Text(tag) },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Remove")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Loan Option
                if (uiState.direction != Transaction.TransactionDirection.TRANSFER) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.isLoan,
                                onCheckedChange = viewModel::updateIsLoan
                            )
                            Text("This is a loan transaction")
                        }
                        
                        if (uiState.isLoan && uiState.counterparty.isNotBlank()) {
                            Text("Loan Type", style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LoanRecord.LoanType.values().forEach { loanType ->
                                    FilterChip(
                                        selected = uiState.loanType == loanType,
                                        onClick = { viewModel.updateLoanType(loanType) },
                                        label = { 
                                            Text(
                                                when (loanType) {
                                                    LoanRecord.LoanType.I_LENT -> "I lent"
                                                    LoanRecord.LoanType.I_BORROWED -> "I borrowed"
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Error message
    if (uiState.errorMessage.isNotEmpty()) {
        LaunchedEffect(uiState.errorMessage) {
            // Show error (in a real app, you'd use a snackbar)
            viewModel.clearError()
        }
    }
}
