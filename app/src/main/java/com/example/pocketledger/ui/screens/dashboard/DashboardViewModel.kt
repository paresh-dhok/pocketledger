package com.example.pocketledger.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.model.Account
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.LoanRepository
import com.example.pocketledger.data.dao.CategorySpending
import com.example.pocketledger.data.dao.DailyBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                accountRepository.getAllAccounts(),
                transactionRepository.getAllTransactions(),
                loanRepository.getActiveLoans()
            ) { accounts, transactions, loans ->
                val totalBalance = accounts.sumOf { it.balance }
                val recentTransactions = transactions.take(5)
                val totalOutstandingLoans = loans.filter { 
                    it.lenderOrBorrower == com.example.pocketledger.data.model.LoanRecord.LoanType.I_BORROWED 
                }.sumOf { it.outstandingAmount }
                val totalOwedToMe = loans.filter { 
                    it.lenderOrBorrower == com.example.pocketledger.data.model.LoanRecord.LoanType.I_LENT 
                }.sumOf { it.outstandingAmount }

                val thirtyDaysAgo = LocalDateTime.now().minusDays(30)
                val expenseByCategory = transactionRepository.getExpenseByCategory(
                    thirtyDaysAgo, LocalDateTime.now()
                ).take(5)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalBalance = totalBalance,
                    accounts = accounts,
                    recentTransactions = recentTransactions,
                    activeLoans = loans,
                    totalOutstandingLoans = totalOutstandingLoans,
                    totalOwedToMe = totalOwedToMe,
                    expenseByCategory = expenseByCategory
                )
            }.collect {
                // Flow collection handled by combine
            }
        }
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadDashboardData()
    }
}

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalBalance: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val activeLoans: List<com.example.pocketledger.data.model.LoanRecord> = emptyList(),
    val totalOutstandingLoans: Double = 0.0,
    val totalOwedToMe: Double = 0.0,
    val expenseByCategory: List<CategorySpending> = emptyList(),
    val balanceOverTime: List<DailyBalance> = emptyList()
)
