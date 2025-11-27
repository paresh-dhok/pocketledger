package com.example.pocketledger.data

import com.example.pocketledger.data.local.entity.AccountEntity
import com.example.pocketledger.data.local.entity.AccountType
import com.example.pocketledger.data.local.entity.TransactionDirection
import com.example.pocketledger.data.local.entity.TransactionEntity
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class DebugDataGenerator @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun generateSampleData() {
        // 1. Create Accounts
        val cash = AccountEntity(name = "Cash", type = AccountType.CASH, balance = BigDecimal("2000"))
        val hdfc = AccountEntity(name = "HDFC", type = AccountType.BANK, balance = BigDecimal("5000"))
        val sbi = AccountEntity(name = "SBI", type = AccountType.BANK, balance = BigDecimal("15000"))

        accountRepository.insertAccount(cash)
        accountRepository.insertAccount(hdfc)
        accountRepository.insertAccount(sbi)

        // 2. Create Transactions
        val categories = listOf("Food", "Transport", "Tuition", "Rent", "Utilities", "Entertainment")
        
        // Add some random transactions
        val transactions = listOf(
            TransactionEntity(
                datetime = LocalDateTime.now().minusDays(1),
                amount = BigDecimal("150"),
                direction = TransactionDirection.EXPENSE,
                fromAccountId = cash.id,
                toAccountId = null,
                category = "Food",
                subcategory = null,
                counterparty = "Burger King",
                note = "Lunch",
                tags = listOf("food"),
                isSettledLoan = false,
                relatedLoanId = null
            ),
            TransactionEntity(
                datetime = LocalDateTime.now().minusDays(2),
                amount = BigDecimal("5000"),
                direction = TransactionDirection.EXPENSE,
                fromAccountId = hdfc.id,
                toAccountId = null,
                category = "Rent",
                subcategory = null,
                counterparty = "Landlord",
                note = "Monthly Rent",
                tags = listOf("rent"),
                isSettledLoan = false,
                relatedLoanId = null
            ),
             TransactionEntity(
                datetime = LocalDateTime.now().minusDays(5),
                amount = BigDecimal("2000"),
                direction = TransactionDirection.INCOME,
                fromAccountId = sbi.id,
                toAccountId = null,
                category = "Salary",
                subcategory = null,
                counterparty = "Company",
                note = "Bonus",
                tags = listOf("income"),
                isSettledLoan = false,
                relatedLoanId = null
            )
        )

        transactions.forEach { 
            transactionRepository.insertTransaction(it)
            // Note: In a real app we'd update balances here too or use the UseCase.
            // For debug generator, we assume balances above were initial states or we should calculate them.
            // Since we set initial balances manually above, we might be double counting if we process them.
            // But for sample data, let's just insert them for history visibility.
        }
    }
}
