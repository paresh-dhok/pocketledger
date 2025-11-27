package com.example.pocketledger.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportToCsv(destinationFile: File) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val transactions = transactionRepository.getAllTransactions().first()
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                
                FileOutputStream(destinationFile).use { output ->
                    // Write CSV header
                    val header = "Date,Amount,Direction,Category,Counterparty,Note,Tags,From Account,To Account\n"
                    output.write(header.toByteArray())
                    
                    // Write transaction data
                    transactions.forEach { transaction ->
                        val date = transaction.dateTime.format(dateFormat)
                        val amount = transaction.amount.toString()
                        val direction = transaction.direction.name
                        val category = transaction.category
                        val counterparty = transaction.counterparty ?: ""
                        val note = transaction.note ?: ""
                        val tags = transaction.tags.joinToString(";")
                        val fromAccount = transaction.fromAccountId
                        val toAccount = transaction.toAccountId ?: ""
                        
                        val line = "\"$date\",\"$amount\",\"$direction\",\"$category\",\"$counterparty\",\"$note\",\"$tags\",\"$fromAccount\",\"$toAccount\"\n"
                        output.write(line.toByteArray())
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Export completed successfully: ${destinationFile.name}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Export failed: ${e.message}"
                )
            }
        }
    }

    fun getDatabaseStats() {
        viewModelScope.launch {
            try {
                val transactions = transactionRepository.getAllTransactions().first()
                val accounts = accountRepository.getAllAccounts().first()
                val loans = loanRepository.getAllLoans().first()
                
                val totalExpenses = transactions
                    .filter { it.direction == com.example.pocketledger.data.model.Transaction.TransactionDirection.EXPENSE }
                    .sumOf { it.amount }
                val totalIncome = transactions
                    .filter { it.direction == com.example.pocketledger.data.model.Transaction.TransactionDirection.INCOME }
                    .sumOf { it.amount }
                
                val stats = DatabaseStats(
                    totalTransactions = transactions.size,
                    totalAccounts = accounts.size,
                    totalLoans = loans.size,
                    totalExpenses = totalExpenses,
                    totalIncome = totalIncome,
                    oldestTransaction = transactions.minOfOrNull { it.dateTime },
                    newestTransaction = transactions.maxOfOrNull { it.dateTime }
                )
                
                _uiState.value = _uiState.value.copy(
                    databaseStats = stats
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load database stats: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = "")
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val databaseStats: DatabaseStats? = null,
    val errorMessage: String = "",
    val successMessage: String = ""
)

data class DatabaseStats(
    val totalTransactions: Int,
    val totalAccounts: Int,
    val totalLoans: Int,
    val totalExpenses: Double,
    val totalIncome: Double,
    val oldestTransaction: LocalDateTime?,
    val newestTransaction: LocalDateTime?
)
