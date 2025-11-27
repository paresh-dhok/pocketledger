package com.example.pocketledger.domain.usecase

import androidx.room.withTransaction
import com.example.pocketledger.data.local.AppDatabase
import com.example.pocketledger.data.local.entity.LoanRecordEntity
import com.example.pocketledger.data.local.entity.TransactionDirection
import com.example.pocketledger.data.local.entity.TransactionEntity
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.LoanRepository
import com.example.pocketledger.data.repository.TransactionRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject

class LoanSettlementUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val loanRepository: LoanRepository,
    private val database: AppDatabase
) {
    suspend fun settleLoan(
        loan: LoanRecordEntity,
        amount: BigDecimal,
        accountId: java.util.UUID,
        note: String?
    ) {
        database.withTransaction {
            // 1. Create Transaction (Repayment)
            // If I LENT, and I get paid back -> INCOME
            // If I BORROWED, and I pay back -> EXPENSE
            val direction = when (loan.lenderOrBorrower) {
                com.example.pocketledger.data.local.entity.LoanType.I_LENT -> TransactionDirection.INCOME
                com.example.pocketledger.data.local.entity.LoanType.I_BORROWED -> TransactionDirection.EXPENSE
            }

            val transaction = TransactionEntity(
                datetime = LocalDateTime.now(),
                amount = amount,
                direction = direction,
                fromAccountId = accountId,
                toAccountId = null,
                category = "Loan Repayment",
                subcategory = null,
                counterparty = loan.counterparty,
                note = note ?: "Settlement for loan to ${loan.counterparty}",
                tags = listOf("loan_settlement"),
                isSettledLoan = true,
                relatedLoanId = loan.id
            )
            transactionRepository.insertTransaction(transaction)

            // 2. Update Account Balance
            val account = accountRepository.getAccountById(accountId)
            if (account != null) {
                val newBalance = if (direction == TransactionDirection.INCOME) {
                    account.balance.add(amount)
                } else {
                    account.balance.subtract(amount)
                }
                accountRepository.updateAccount(account.copy(balance = newBalance))
            }

            // 3. Update Loan Outstanding Amount
            val newOutstanding = loan.outstandingAmount.subtract(amount)
            loanRepository.updateLoan(loan.copy(outstandingAmount = newOutstanding))
        }
    }
}
