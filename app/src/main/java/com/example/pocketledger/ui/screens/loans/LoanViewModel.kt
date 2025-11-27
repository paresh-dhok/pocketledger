package com.example.pocketledger.ui.screens.loans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.model.LoanRecord
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.repository.LoanRepository
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoansUiState())
    val uiState: StateFlow<LoansUiState> = _uiState.asStateFlow()

    init {
        loadLoans()
    }

    private fun loadLoans() {
        viewModelScope.launch {
            combine(
                loanRepository.getAllLoans(),
                loanRepository.getActiveLoans(),
                accountRepository.getAllAccounts()
            ) { allLoans, activeLoans, accounts ->
                val totalOwed = activeLoans
                    .filter { it.lenderOrBorrower == LoanRecord.LoanType.I_BORROWED }
                    .sumOf { it.outstandingAmount }
                val totalOwedToMe = activeLoans
                    .filter { it.lenderOrBorrower == LoanRecord.LoanType.I_LENT }
                    .sumOf { it.outstandingAmount }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allLoans = allLoans,
                    activeLoans = activeLoans,
                    accounts = accounts,
                    totalOwed = totalOwed,
                    totalOwedToMe = totalOwedToMe
                )
            }.collect {
                // Flow collection handled by combine
            }
        }
    }

    fun createLoan(
        counterparty: String,
        amount: Double,
        type: LoanRecord.LoanType,
        accountId: String
    ) {
        viewModelScope.launch {
            try {
                val loan = loanRepository.createLoan(
                    counterparty = counterparty,
                    amount = amount,
                    type = type
                )

                // Create corresponding transaction
                val transaction = Transaction(
                    fromAccountId = accountId,
                    amount = amount,
                    direction = when (type) {
                        LoanRecord.LoanType.I_LENT -> Transaction.TransactionDirection.EXPENSE
                        LoanRecord.LoanType.I_BORROWED -> Transaction.TransactionDirection.INCOME
                    },
                    category = "Loan",
                    counterparty = counterparty,
                    isSettledLoan = true,
                    relatedLoanId = loan.id
                )
                
                transactionRepository.insertTransaction(transaction)
                
                _uiState.value = _uiState.value.copy(
                    isSuccess = true,
                    successMessage = "Loan created successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to create loan"
                )
            }
        }
    }

    fun makePayment(loanId: String, paymentAmount: Double, accountId: String) {
        viewModelScope.launch {
            try {
                val loan = loanRepository.getLoanById(loanId)
                if (loan == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Loan not found"
                    )
                    return@launch
                }

                if (paymentAmount > loan.outstandingAmount) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Payment amount exceeds outstanding amount"
                    )
                    return@launch
                }

                // Create payment transaction
                val paymentTransaction = Transaction(
                    fromAccountId = accountId,
                    amount = paymentAmount,
                    direction = when (loan.lenderOrBorrower) {
                        LoanRecord.LoanType.I_LENT -> Transaction.TransactionDirection.INCOME
                        LoanRecord.LoanType.I_BORROWED -> Transaction.TransactionDirection.EXPENSE
                    },
                    category = "Loan Payment",
                    counterparty = loan.counterparty,
                    isSettledLoan = true,
                    relatedLoanId = loanId
                )

                transactionRepository.insertTransaction(paymentTransaction)
                
                // Update loan outstanding amount
                loanRepository.makeLoanPayment(loanId, paymentAmount, paymentTransaction)

                _uiState.value = _uiState.value.copy(
                    isSuccess = true,
                    successMessage = "Payment recorded successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to record payment"
                )
            }
        }
    }

    fun settleLoan(loanId: String, accountId: String) {
        viewModelScope.launch {
            try {
                val loan = loanRepository.getLoanById(loanId)
                if (loan == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Loan not found"
                    )
                    return@launch
                }

                if (loan.outstandingAmount <= 0) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Loan is already settled"
                    )
                    return@launch
                }

                // Create settlement transaction
                val settlementTransaction = Transaction(
                    fromAccountId = accountId,
                    amount = loan.outstandingAmount,
                    direction = when (loan.lenderOrBorrower) {
                        LoanRecord.LoanType.I_LENT -> Transaction.TransactionDirection.INCOME
                        LoanRecord.LoanType.I_BORROWED -> Transaction.TransactionDirection.EXPENSE
                    },
                    category = "Loan Settlement",
                    counterparty = loan.counterparty,
                    isSettledLoan = true,
                    relatedLoanId = loanId
                )

                transactionRepository.insertTransaction(settlementTransaction)
                
                // Update loan outstanding amount to 0
                loanRepository.updateOutstandingAmount(loanId, 0.0)

                _uiState.value = _uiState.value.copy(
                    isSuccess = true,
                    successMessage = "Loan settled successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to settle loan"
                )
            }
        }
    }

    fun deleteLoan(loanId: String) {
        viewModelScope.launch {
            try {
                val loan = loanRepository.getLoanById(loanId)
                if (loan == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Loan not found"
                    )
                    return@launch
                }

                if (loan.outstandingAmount > 0) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Cannot delete loan with outstanding amount"
                    )
                    return@launch
                }

                loanRepository.deleteLoanById(loanId)
                
                _uiState.value = _uiState.value.copy(
                    isSuccess = true,
                    successMessage = "Loan deleted successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to delete loan"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
            successMessage = ""
        )
    }
}

data class LoansUiState(
    val isLoading: Boolean = true,
    val allLoans: List<LoanRecord> = emptyList(),
    val activeLoans: List<LoanRecord> = emptyList(),
    val accounts: List<com.example.pocketledger.data.model.Account> = emptyList(),
    val totalOwed: Double = 0.0,
    val totalOwedToMe: Double = 0.0,
    val errorMessage: String = "",
    val isSuccess: Boolean = false,
    val successMessage: String = ""
)
