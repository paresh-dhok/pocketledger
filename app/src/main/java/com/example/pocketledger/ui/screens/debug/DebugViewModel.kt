package com.example.pocketledger.ui.screens.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.seeder.DataSeeder
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val dataSeeder: DataSeeder,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DebugUiState())
    val uiState: StateFlow<DebugUiState> = _uiState.asStateFlow()

    fun seedSampleData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = ""
            )
            
            try {
                dataSeeder.seedSampleData()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Sample data seeded successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error seeding data: ${e.message}"
                )
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = ""
            )
            
            try {
                // Clear all data (in reverse order of dependencies)
                transactionRepository.getAllTransactions().first().forEach { 
                    transactionRepository.deleteTransaction(it) 
                }
                loanRepository.getAllLoans().first().forEach { 
                    loanRepository.deleteLoan(it) 
                }
                accountRepository.getAllAccounts().first().forEach { 
                    accountRepository.deleteAccount(it) 
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "All data cleared successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error clearing data: ${e.message}"
                )
            }
        }
    }
}

data class DebugUiState(
    val isLoading: Boolean = false,
    val message: String = ""
)
