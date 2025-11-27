package com.example.pocketledger.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.local.entity.AccountEntity
import com.example.pocketledger.data.local.entity.AccountType
import com.example.pocketledger.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    fun showAddAccountDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddAccountDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false, newAccountName = "", newAccountBalance = "")
    }

    fun onNewAccountNameChange(name: String) {
        _uiState.value = _uiState.value.copy(newAccountName = name)
    }

    fun onNewAccountBalanceChange(balance: String) {
        _uiState.value = _uiState.value.copy(newAccountBalance = balance)
    }

    fun onNewAccountTypeChange(type: AccountType) {
        _uiState.value = _uiState.value.copy(newAccountType = type)
    }

    fun addAccount() {
        val state = _uiState.value
        if (state.newAccountName.isBlank()) return

        val balance = if (state.newAccountBalance.isBlank()) BigDecimal.ZERO else BigDecimal(state.newAccountBalance)

        viewModelScope.launch {
            val account = AccountEntity(
                name = state.newAccountName,
                type = state.newAccountType,
                balance = balance
            )
            accountRepository.insertAccount(account)
            hideAddAccountDialog()
        }
    }
}

data class AccountsUiState(
    val showAddDialog: Boolean = false,
    val newAccountName: String = "",
    val newAccountBalance: String = "",
    val newAccountType: AccountType = AccountType.CASH
)
