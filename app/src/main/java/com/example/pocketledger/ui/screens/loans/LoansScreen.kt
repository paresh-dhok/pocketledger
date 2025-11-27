package com.example.pocketledger.ui.screens.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketledger.data.model.LoanRecord
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    onNavigateBack: () -> Unit,
    viewModel: LoanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddLoanDialog by remember { mutableStateOf(false) }
    var selectedLoan by remember { mutableStateOf<LoanRecord?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val dateFormat = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loans") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddLoanDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Loan")
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // I Owe Card
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "I Owe",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currencyFormat.format(uiState.totalOwed),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Owed to Me Card
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Owed to Me",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currencyFormat.format(uiState.totalOwedToMe),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Loans List
                if (uiState.activeLoans.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Handshake,
                                modifier = Modifier.size(64.dp),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No active loans",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.activeLoans) { loan ->
                            LoanItem(
                                loan = loan,
                                currencyFormat = currencyFormat,
                                dateFormat = dateFormat,
                                onPayment = {
                                    selectedLoan = loan
                                    showPaymentDialog = true
                                },
                                onSettle = {
                                    selectedLoan = loan
                                    // Show settle confirmation
                                },
                                onDelete = {
                                    selectedLoan = loan
                                    // Show delete confirmation
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Loan Dialog
    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onAddLoan = { counterparty, amount, type, accountId ->
                viewModel.createLoan(counterparty, amount, type, accountId)
                showAddLoanDialog = false
            },
            accounts = uiState.accounts
        )
    }

    // Payment Dialog
    if (showPaymentDialog && selectedLoan != null) {
        PaymentDialog(
            loan = selectedLoan!!,
            currencyFormat = currencyFormat,
            accounts = uiState.accounts,
            onDismiss = { 
                showPaymentDialog = false
                selectedLoan = null
            },
            onPayment = { paymentAmount, accountId ->
                viewModel.makePayment(selectedLoan!!.id, paymentAmount, accountId)
                showPaymentDialog = false
                selectedLoan = null
            },
            onSettle = { accountId ->
                viewModel.settleLoan(selectedLoan!!.id, accountId)
                showPaymentDialog = false
                selectedLoan = null
            }
        )
    }

    // Handle success/error messages
    LaunchedEffect(uiState.isSuccess, uiState.successMessage) {
        if (uiState.isSuccess) {
            // Show success message
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            // Show error message
            viewModel.clearError()
        }
    }
}

@Composable
fun LoanItem(
    loan: LoanRecord,
    currencyFormat: NumberFormat,
    dateFormat: DateTimeFormatter,
    onPayment: () -> Unit,
    onSettle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = loan.counterparty,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = when (loan.lenderOrBorrower) {
                            LoanRecord.LoanType.I_LENT -> "I lent"
                            LoanRecord.LoanType.I_BORROWED -> "I borrowed"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Created: ${loan.createdAt.format(dateFormat)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = currencyFormat.format(loan.outstandingAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (loan.lenderOrBorrower == LoanRecord.LoanType.I_BORROWED) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "of ${currencyFormat.format(loan.originalAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (loan.outstandingAmount > 0) {
                    OutlinedButton(
                        onClick = onPayment,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Payment")
                    }
                    
                    Button(
                        onClick = onSettle,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Settle")
                    }
                } else {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun AddLoanDialog(
    onDismiss: () -> Unit,
    onAddLoan: (String, Double, LoanRecord.LoanType, String) -> Unit,
    accounts: List<com.example.pocketledger.data.model.Account>
) {
    var counterparty by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(LoanRecord.LoanType.I_BORROWED) }
    var selectedAccountId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Loan") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = counterparty,
                    onValueChange = { counterparty = it },
                    label = { Text("Counterparty*") },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount*") },
                    singleLine = true
                )
                
                Text("Loan Type")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = type == LoanRecord.LoanType.I_BORROWED,
                            onClick = { type = LoanRecord.LoanType.I_BORROWED }
                        )
                        Text("I borrowed")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = type == LoanRecord.LoanType.I_LENT,
                            onClick = { type = LoanRecord.LoanType.I_LENT }
                        )
                        Text("I lent")
                    }
                }
                
                if (accounts.isNotEmpty()) {
                    Text("Account*")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.forEach { account ->
                            FilterChip(
                                selected = selectedAccountId == account.id,
                                onClick = { selectedAccountId = account.id },
                                label = { Text(account.name) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (counterparty.isNotBlank() && amountValue != null && amountValue > 0 && selectedAccountId.isNotBlank()) {
                        onAddLoan(counterparty, amountValue, type, selectedAccountId)
                    }
                },
                enabled = counterparty.isNotBlank() && 
                         amount.toDoubleOrNull() != null && 
                         amount.toDoubleOrNull()!! > 0 && 
                         selectedAccountId.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PaymentDialog(
    loan: LoanRecord,
    currencyFormat: NumberFormat,
    accounts: List<com.example.pocketledger.data.model.Account>,
    onDismiss: () -> Unit,
    onPayment: (Double, String) -> Unit,
    onSettle: (String) -> Unit
) {
    var paymentAmount by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Loan Payment") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Outstanding: ${currencyFormat.format(loan.outstandingAmount)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                OutlinedTextField(
                    value = paymentAmount,
                    onValueChange = { paymentAmount = it },
                    label = { Text("Payment Amount") },
                    singleLine = true
                )
                
                if (accounts.isNotEmpty()) {
                    Text("Account*")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.forEach { account ->
                            FilterChip(
                                selected = selectedAccountId == account.id,
                                onClick = { selectedAccountId = account.id },
                                label = { Text(account.name) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column {
                Button(
                    onClick = {
                        val amount = paymentAmount.toDoubleOrNull()
                        if (amount != null && amount > 0 && selectedAccountId.isNotBlank()) {
                            onPayment(amount, selectedAccountId)
                        }
                    },
                    enabled = paymentAmount.toDoubleOrNull() != null && 
                             paymentAmount.toDoubleOrNull()!! > 0 && 
                             selectedAccountId.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Make Payment")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        if (selectedAccountId.isNotBlank()) {
                            onSettle(selectedAccountId)
                        }
                    },
                    enabled = selectedAccountId.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Settle Full Amount (${currencyFormat.format(loan.outstandingAmount)})")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
