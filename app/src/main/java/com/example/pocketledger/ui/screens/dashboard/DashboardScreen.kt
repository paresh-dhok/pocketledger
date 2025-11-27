package com.example.pocketledger.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.ui.components.*
import com.example.pocketledger.ui.navigation.Screen
import com.example.pocketledger.ui.theme.PocketLedgerTheme
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDebug: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PocketLedger") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Accounts") },
                    label = { Text("Accounts") },
                    selected = false,
                    onClick = onNavigateToAccounts
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Transactions") },
                    label = { Text("Transactions") },
                    selected = false,
                    onClick = onNavigateToTransactions
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Handshake, contentDescription = "Loans") },
                    label = { Text("Loans") },
                    selected = false,
                    onClick = onNavigateToLoans
                )
            }
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
                // Total Balance Card
                item {
                    BalanceCard(
                        totalBalance = uiState.totalBalance,
                        currencyFormat = currencyFormat,
                        onLongClick = onNavigateToDebug
                    )
                }

                // Quick Stats Row
                item {
                    QuickStatsRow(
                        totalOutstandingLoans = uiState.totalOutstandingLoans,
                        totalOwedToMe = uiState.totalOwedToMe,
                        currencyFormat = currencyFormat
                    )
                }

                // Recent Transactions
                item {
                    SectionHeader(
                        title = "Recent Transactions",
                        onSeeAllClick = onNavigateToTransactions
                    )
                }
                items(uiState.recentTransactions.take(5)) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        currencyFormat = currencyFormat
                    )
                }

                // Expense by Category (Mini Chart)
                if (uiState.expenseByCategory.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Top Categories (30 days)",
                            onSeeAllClick = null
                        )
                    }
                    items(uiState.expenseByCategory.take(3)) { categorySpending ->
                        CategorySpendingItem(
                            category = categorySpending.category,
                            amount = categorySpending.total,
                            currencyFormat = currencyFormat
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(
    totalBalance: Double,
    currencyFormat: NumberFormat,
    onLongClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { modifier ->
                if (onLongClick != null) {
                    modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongClick() }
                        )
                    }
                } else modifier
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currencyFormat.format(totalBalance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (totalBalance >= 0) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    totalOutstandingLoans: Double,
    totalOwedToMe: Double,
    currencyFormat: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // I Owe Card
        Card(
            modifier = Modifier.weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "I Owe",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormat.format(totalOutstandingLoans),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Owed to Me Card
        Card(
            modifier = Modifier.weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Owed to Me",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormat.format(totalOwedToMe),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        onSeeAllClick?.let {
            TextButton(onClick = it) {
                Text("See All")
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                transaction.counterparty?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = when (transaction.direction) {
                    Transaction.TransactionDirection.EXPENSE -> 
                        "-${currencyFormat.format(transaction.amount)}"
                    Transaction.TransactionDirection.INCOME -> 
                        "+${currencyFormat.format(transaction.amount)}"
                    Transaction.TransactionDirection.TRANSFER -> 
                        currencyFormat.format(transaction.amount)
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = when (transaction.direction) {
                    Transaction.TransactionDirection.EXPENSE -> MaterialTheme.colorScheme.error
                    Transaction.TransactionDirection.INCOME -> MaterialTheme.colorScheme.primary
                    Transaction.TransactionDirection.TRANSFER -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun CategorySpendingItem(
    category: String,
    amount: Double,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = currencyFormat.format(amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
