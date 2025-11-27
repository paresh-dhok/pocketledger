package com.example.pocketledger.domain.usecase

import androidx.room.withTransaction
import com.example.pocketledger.data.database.AppDatabase
import com.example.pocketledger.data.model.TransactionDirection
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.TransactionRepository
import javax.inject.Inject

class ProcessTransactionUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val database: AppDatabase
) {
    suspend operator fun invoke(transaction: Transaction) {
        database.withTransaction {
            // 0. Validate accounts exist
            val from Account = accountRepository.getAccountById(transaction.fromAccountId)
                ?: throw AccountNotFoundException("Source account not found: ${transaction.fromAccountId}")
            
            if (transaction.direction == TransactionDirection.TRANSFER && transaction.toAccountId != null) {
                val toAccount = accountRepository.getAccountById(transaction.toAccountId)
                    ?: throw AccountNotFoundException("Destination account not found: ${transaction.toAccountId}")
            }
            
            // 1. Insert Transaction
            transactionRepository.insertTransaction(transaction)

            // 2. Update Balances
            when (transaction.direction) {
                TransactionDirection.EXPENSE -> {
                    val account = accountRepository.getAccountById(transaction.fromAccountId)!!
                    val newBalance = account.balance - transaction.amount
                    
                    // Validate balance won't go negative
                    if (newBalance < 0) {
                        throw InsufficientBalanceException(
                            "Insufficient balance in account ${account.name}. Current: ${account.balance}, Required: ${transaction.amount}"
                        )
                    }
                    
                    accountRepository.updateAccount(account.copy(balance = newBalance))
                }
                TransactionDirection.INCOME -> {
                    val account = accountRepository.getAccountById(transaction.fromAccountId)!!
                    val newBalance = account.balance + transaction.amount
                    accountRepository.updateAccount(account.copy(balance = newBalance))
                }
                TransactionDirection.TRANSFER -> {
                    // Subtract from Source
                    val fromAccount = accountRepository.getAccountById(transaction.fromAccountId)!!
                    val newFromBalance = fromAccount.balance - transaction.amount
                    
                    // Validate balance won't go negative
                    if (newFromBalance < 0) {
                        throw InsufficientBalanceException(
                            "Insufficient balance in source account ${fromAccount.name}. Current: ${fromAccount.balance}, Required: ${transaction.amount}"
                        )
                    }
                    
                    accountRepository.updateAccount(fromAccount.copy(balance = newFromBalance))

                    // Add to Destination
                    if (transaction.toAccountId != null) {
                        val toAccount = accountRepository.getAccountById(transaction.toAccountId)!!
                        val newToBalance = toAccount.balance + transaction.amount
                        accountRepository.updateAccount(toAccount.copy(balance = newToBalance))
                    }
                }
            }
        }
    }
}

class AccountNotFoundException(message: String) : Exception(message)
class InsufficientBalanceException(message: String) : Exception(message)
