package com.example.pocketledger.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketledger.data.model.Account
import com.example.pocketledger.data.model.TransactionDirection
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val processTransactionUseCase: com.example.pocketledger.domain.usecase.ProcessTransactionUseCase,
    accountRepository: AccountRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onAmountChange(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun onDirectionChange(direction: TransactionDirection) {
        _uiState.value = _uiState.value.copy(direction = direction)
    }

    fun onFromAccountChange(account: Account) {
        _uiState.value = _uiState.value.copy(fromAccount = account)
    }

    fun onToAccountChange(account: Account) {
        _uiState.value = _uiState.value.copy(toAccount = account)
    }

    fun onCategoryChange(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun onDateTimeChange(dateTime: LocalDateTime) {
        _uiState.value = _uiState.value.copy(datetime = dateTime)
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.amount.isBlank() || state.fromAccount == null || state.category.isBlank()) return

        // Basic validation
        val amountValue = BigDecimal(state.amount)
        if (amountValue <= BigDecimal.ZERO) return

        viewModelScope.launch {
            val transaction = Transaction(
                datetime = state.datetime,
                amount = amountValue,
                direction = state.direction,
                fromAccountId = state.fromAccount!!.id,
                toAccountId = if (state.direction == TransactionDirection.TRANSFER) state.toAccount?.id else null,
                category = state.category,
                subcategory = null,
                counterparty = null,
                note = state.note,
                tags = emptyList(),
                isSettledLoan = false,
                relatedLoanId = null
            )
            
            processTransactionUseCase(transaction)
            
            onSuccess()
        }
    }
}

data class AddTransactionUiState(
    val amount: String = "",
    val direction: TransactionDirection = TransactionDirection.EXPENSE,
    val fromAccount: Account? = null,
    val toAccount: Account? = null,
    val category: String = "",
    val note: String = "",
    val datetime: LocalDateTime = LocalDateTime.now()
)
