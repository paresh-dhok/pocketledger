package com.example.pocketledger.domain.usecase

import androidx.room.withTransaction
import com.example.pocketledger.data.local.AppDatabase
import com.example.pocketledger.data.local.entity.TransactionDirection
import com.example.pocketledger.data.local.entity.TransactionEntity
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import javax.inject.Inject

class ProcessTransactionUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val database: AppDatabase
) {
    suspend operator fun invoke(transaction: TransactionEntity) {
        database.withTransaction {
            // 1. Insert Transaction
            transactionRepository.insertTransaction(transaction)

            // 2. Update Balances
            when (transaction.direction) {
                TransactionDirection.EXPENSE -> {
                    val account = accountRepository.getAccountById(transaction.fromAccountId)
                    if (account != null) {
                        val newBalance = account.balance.subtract(transaction.amount)
                        accountRepository.updateAccount(account.copy(balance = newBalance))
                    }
                }
                TransactionDirection.INCOME -> {
                    val account = accountRepository.getAccountById(transaction.fromAccountId)
                    if (account != null) {
                        val newBalance = account.balance.add(transaction.amount)
                        accountRepository.updateAccount(account.copy(balance = newBalance))
                    }
                }
                TransactionDirection.TRANSFER -> {
                    // Subtract from Source
                    val fromAccount = accountRepository.getAccountById(transaction.fromAccountId)
                    if (fromAccount != null) {
                        val newBalance = fromAccount.balance.subtract(transaction.amount)
                        accountRepository.updateAccount(fromAccount.copy(balance = newBalance))
                    }

                    // Add to Destination
                    if (transaction.toAccountId != null) {
                        val toAccount = accountRepository.getAccountById(transaction.toAccountId)
                        if (toAccount != null) {
                            val newBalance = toAccount.balance.add(transaction.amount)
                            accountRepository.updateAccount(toAccount.copy(balance = newBalance))
                        }
                    }
                }
            }
        }
    }
}
