package com.example.pocketledger.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            combine(
                transactionRepository.getAllTransactions(),
                accountRepository.getAllAccounts()
            ) { transactions, accounts ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    transactions = transactions,
                    accounts = accounts,
                    filteredTransactions = transactions
                )
            }.collect {
                // Flow collection handled by combine
            }
        }
    }

    fun searchTransactions(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    filteredTransactions = _uiState.value.transactions
                )
            } else {
                transactionRepository.searchTransactions(query).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        filteredTransactions = results,
                        searchQuery = query
                    )
                }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            if (category == "All") {
                _uiState.value = _uiState.value.copy(
                    filteredTransactions = _uiState.value.transactions,
                    selectedCategory = category
                )
            } else {
                transactionRepository.getTransactionsByCategory(category).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        filteredTransactions = results,
                        selectedCategory = category
                    )
                }
            }
        }
    }

    fun filterByDirection(direction: Transaction.TransactionDirection?) {
        viewModelScope.launch {
            if (direction == null) {
                _uiState.value = _uiState.value.copy(
                    filteredTransactions = _uiState.value.transactions,
                    selectedDirection = null
                )
            } else {
                transactionRepository.getTransactionsByDirection(direction).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        filteredTransactions = results,
                        selectedDirection = direction
                    )
                }
            }
        }
    }

    fun getCategories(): List<String> {
        val categories = _uiState.value.transactions
            .map { it.category }
            .distinct()
            .sorted()
        return listOf("All") + categories
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadTransactions()
    }
}

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val accounts: List<com.example.pocketledger.data.model.Account> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedDirection: Transaction.TransactionDirection? = null
)
