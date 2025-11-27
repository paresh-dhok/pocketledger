package com.example.pocketledger.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.model.Account
import com.example.pocketledger.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect { accounts ->
                val totalBalance = accounts.sumOf { it.balance }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    accounts = accounts,
                    totalBalance = totalBalance
                )
            }
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.insertAccount(account)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.updateAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            val transactionCount = accountRepository.getTransactionCount(account.id)
            if (transactionCount == 0) {
                accountRepository.deleteAccount(account)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Cannot delete account with existing transactions. Please delete or move transactions first."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}

data class AccountsUiState(
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val errorMessage: String = ""
)
