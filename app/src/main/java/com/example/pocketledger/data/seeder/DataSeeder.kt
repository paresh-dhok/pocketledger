package com.example.pocketledger.data.seeder

import com.example.pocketledger.data.model.Account
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.model.LoanRecord
import com.example.pocketledger.data.model.RecurringRule
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.LoanRepository
import com.example.pocketledger.data.repository.RecurringRuleRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSeeder @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val loanRepository: LoanRepository
) {
    
    suspend fun seedSampleData() {
        // Check if data already exists
        val existingAccounts = accountRepository.getAllAccounts().first()
        if (existingAccounts.isNotEmpty()) {
            return // Data already seeded
        }

        // Create sample accounts
        val cashAccount = Account(
            name = "Cash",
            type = Account.AccountType.CASH,
            balance = 2000.0
        )
        val hdfcAccount = Account(
            name = "HDFC Bank",
            type = Account.AccountType.BANK,
            balance = 5000.0
        )
        val sbiAccount = Account(
            name = "SBI Bank",
            type = Account.AccountType.BANK,
            balance = 15000.0
        )

        accountRepository.insertAccount(cashAccount)
        accountRepository.insertAccount(hdfcAccount)
        accountRepository.insertAccount(sbiAccount)

        // Create sample transactions
        val transactions = listOf(
            Transaction(
                dateTime = LocalDateTime.now().minusDays(1),
                amount = 250.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = cashAccount.id,
                category = "Food",
                counterparty = "Cafe XYZ",
                note = "Lunch with friends",
                tags = listOf("food", "friends")
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(2),
                amount = 1000.0,
                direction = Transaction.TransactionDirection.INCOME,
                fromAccountId = hdfcAccount.id,
                category = "Salary",
                note = "Monthly salary"
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(3),
                amount = 500.0,
                direction = Transaction.TransactionDirection.TRANSFER,
                fromAccountId = sbiAccount.id,
                toAccountId = cashAccount.id,
                category = "Transfer",
                note = "Transfer to cash for expenses"
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(5),
                amount = 150.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = hdfcAccount.id,
                category = "Transport",
                counterparty = "Uber",
                tags = listOf("transport", "work")
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(7),
                amount = 2000.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = sbiAccount.id,
                category = "Rent",
                counterparty = "Landlord",
                note = "Monthly rent"
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(10),
                amount = 300.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = cashAccount.id,
                category = "Entertainment",
                counterparty = "Netflix",
                tags = listOf("entertainment", "subscription")
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(12),
                amount = 100.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = hdfcAccount.id,
                category = "Utilities",
                counterparty = "Electricity Board",
                note = "Electricity bill"
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(15),
                amount = 500.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = cashAccount.id,
                category = "Tuition",
                counterparty = "ABC Academy",
                tags = listOf("education", "tuition")
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(20),
                amount = 50.0,
                direction = Transaction.TransactionDirection.EXPENSE,
                fromAccountId = hdfcAccount.id,
                category = "Food",
                counterparty = "Grocery Store",
                tags = listOf("food", "groceries")
            ),
            Transaction(
                dateTime = LocalDateTime.now().minusDays(25),
                amount = 750.0,
                direction = Transaction.TransactionDirection.INCOME,
                fromAccountId = sbiAccount.id,
                category = "Freelance",
                counterparty = "Client ABC",
                note = "Freelance project payment"
            )
        )

        transactions.forEach { transactionRepository.insertTransaction(it) }

        // Create sample loans
        val loanToFriend = loanRepository.createLoan(
            counterparty = "John Doe",
            amount = 1000.0,
            type = LoanRecord.LoanType.I_LENT
        )

        val loanFromFriend = loanRepository.createLoan(
            counterparty = "Jane Smith",
            amount = 500.0,
            type = LoanRecord.LoanType.I_BORROWED
        )

        println("Sample data seeded successfully!")
    }
}
