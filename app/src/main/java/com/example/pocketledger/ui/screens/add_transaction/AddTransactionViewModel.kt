package com.example.pocketledger.ui.screens.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.model.Account
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.model.LoanRecord
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
        loadRecentCategories()
        loadRecentCounterparties()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect { accounts ->
                _uiState.value = _uiState.value.copy(
                    accounts = accounts,
                    isLoading = false
                )
            }
        }
    }

    private fun loadRecentCategories() {
        viewModelScope.launch {
            // Get recent transactions to extract categories
            transactionRepository.getAllTransactions().collect { transactions ->
                val categories = transactions
                    .map { it.category }
                    .distinct()
                    .sorted()
                    .take(10)
                _uiState.value = _uiState.value.copy(recentCategories = categories)
            }
        }
    }

    private fun loadRecentCounterparties() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                val counterparties = transactions
                    .mapNotNull { it.counterparty }
                    .distinct()
                    .sorted()
                    .take(10)
                _uiState.value = _uiState.value.copy(recentCounterparties = counterparties)
            }
        }
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateDirection(direction: Transaction.TransactionDirection) {
        _uiState.value = _uiState.value.copy(
            direction = direction,
            toAccountId = if (direction != Transaction.TransactionDirection.TRANSFER) null else _uiState.value.toAccountId
        )
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateCounterparty(counterparty: String) {
        _uiState.value = _uiState.value.copy(counterparty = counterparty)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun updateFromAccount(accountId: String) {
        _uiState.value = _uiState.value.copy(fromAccountId = accountId)
    }

    fun updateToAccount(accountId: String) {
        _uiState.value = _uiState.value.copy(toAccountId = accountId)
    }

    fun updateTags(tags: List<String>) {
        _uiState.value = _uiState.value.copy(tags = tags)
    }

    fun updateIsLoan(isLoan: Boolean) {
        _uiState.value = _uiState.value.copy(isLoan = isLoan)
    }

    fun updateLoanType(loanType: LoanRecord.LoanType) {
        _uiState.value = _uiState.value.copy(loanType = loanType)
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && !_uiState.value.tags.contains(tag)) {
            _uiState.value = _uiState.value.copy(
                tags = _uiState.value.tags + tag
            )
        }
    }

    fun removeTag(tag: String) {
        _uiState.value = _uiState.value.copy(
            tags = _uiState.value.tags - tag
        )
    }

    fun saveTransaction(): Result<Unit> {
        val state = _uiState.value
        
        // Validation
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            return Result.failure(Exception("Please enter a valid amount"))
        }

        if (state.fromAccountId.isBlank()) {
            return Result.failure(Exception("Please select an account"))
        }

        if (state.direction == Transaction.TransactionDirection.TRANSFER && state.toAccountId.isBlank()) {
            return Result.failure(Exception("Please select destination account for transfer"))
        }

        if (state.direction == Transaction.TransactionDirection.TRANSFER && state.fromAccountId == state.toAccountId) {
            return Result.failure(Exception("Source and destination accounts must be different"))
        }

        if (state.category.isBlank()) {
            return Result.failure(Exception("Please enter a category"))
        }

        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    dateTime = LocalDateTime.now(),
                    amount = amount,
                    direction = state.direction,
                    fromAccountId = state.fromAccountId,
                    toAccountId = state.toAccountId,
                    category = state.category,
                    counterparty = state.counterparty.ifBlank { null },
                    note = state.note.ifBlank { null },
                    tags = state.tags,
                    isSettledLoan = state.isLoan
                )

                transactionRepository.insertTransaction(transaction)

                // Create loan record if it's a loan transaction
                if (state.isLoan && state.counterparty.isNotBlank()) {
                    val loan = loanRepository.createLoan(
                        counterparty = state.counterparty,
                        amount = amount,
                        type = state.loanType,
                        relatedTransactionId = transaction.id
                    )
                    // Update transaction with loan reference
                    val updatedTransaction = transaction.copy(
                        relatedLoanId = loan.id,
                        isSettledLoan = true
                    )
                    transactionRepository.updateTransaction(updatedTransaction)
                }

                _uiState.value = _uiState.value.copy(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to save transaction"
                )
            }
        }

        return Result.success(Unit)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun resetForm() {
        _uiState.value = AddTransactionUiState(
            accounts = _uiState.value.accounts,
            recentCategories = _uiState.value.recentCategories,
            recentCounterparties = _uiState.value.recentCounterparties
        )
    }
}

data class AddTransactionUiState(
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val recentCategories: List<String> = emptyList(),
    val recentCounterparties: List<String> = emptyList(),
    val amount: String = "",
    val direction: Transaction.TransactionDirection = Transaction.TransactionDirection.EXPENSE,
    val category: String = "",
    val counterparty: String = "",
    val note: String = "",
    val tags: List<String> = emptyList(),
    val fromAccountId: String = "",
    val toAccountId: String = "",
    val isLoan: Boolean = false,
    val loanType: LoanRecord.LoanType = LoanRecord.LoanType.I_LENT,
    val errorMessage: String = "",
    val isSuccess: Boolean = false
)
