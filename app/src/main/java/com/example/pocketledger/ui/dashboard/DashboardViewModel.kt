package com.example.pocketledger.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.local.entity.AccountEntity
import com.example.pocketledger.data.local.entity.TransactionEntity
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository,
    private val debugDataGenerator: com.example.pocketledger.data.DebugDataGenerator
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = transactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBalance: StateFlow<BigDecimal> = accounts.combine(accounts) { accs, _ ->
        accs.fold(BigDecimal.ZERO) { sum, account -> sum.add(account.balance) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BigDecimal.ZERO)

    fun generateSampleData() {
        viewModelScope.launch {
            debugDataGenerator.generateSampleData()
        }
    }
}
