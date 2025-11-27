package com.example.pocketledger.domain.usecase

import androidx.room.withTransaction
import com.example.pocketledger.data.database.AppDatabase
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.model.TransactionDirection
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for deleting transactions with proper balance reversal.
 * Ensures account balances are correctly updated when transactions are removed.
 */
class ReverseTransactionUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val database: AppDatabase
) {
    suspend operator fun invoke(transaction: Transaction) {
        database.withTransaction {
            // 1. Reverse Balance Changes
            when (transaction.direction) {
                TransactionDirection.EXPENSE -> {
                    // Add money back to the account
                    val account = accountRepository.getAccountById(transaction.fromAccountId)
                    if (account != null) {
                        val newBalance = account.balance + transaction.amount
                        accountRepository.updateAccount(account.copy(balance = newBalance))
                    }
                }
                TransactionDirection.INCOME -> {
                    // Remove money from the account
                    val account = accountRepository.getAccountById(transaction.fromAccountId)
                    if (account != null) {
                        val newBalance = account.balance - transaction.amount
                        accountRepository.updateAccount(account.copy(balance = newBalance))
                    }
                }
                TransactionDirection.TRANSFER -> {
                    // Add back to source account
                    val fromAccount = accountRepository.getAccountById(transaction.fromAccountId)
                    if (fromAccount != null) {
                        val newBalance = fromAccount.balance + transaction.amount
                        accountRepository.updateAccount(fromAccount.copy(balance = newBalance))
                    }

                    // Remove from destination account
                    if (transaction.toAccountId != null) {
                        val toAccount = accountRepository.getAccountById(transaction.toAccountId)
                        if (toAccount != null) {
                            val newBalance = toAccount.balance - transaction.amount
                            accountRepository.updateAccount(toAccount.copy(balance = newBalance))
                        }
                    }
                }
            }

            // 2. Delete the transaction
            transactionRepository.deleteTransaction(transaction)
        }
    }
}
