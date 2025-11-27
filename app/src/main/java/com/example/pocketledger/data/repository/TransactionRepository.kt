package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.dao.CategorySpending
import com.example.pocketledger.data.dao.DailyBalance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: com.example.pocketledger.data.dao.TransactionDao,
    private val accountDao: com.example.pocketledger.data.dao.AccountDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByAccount(accountId)
    
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByCategory(category)
    
    fun getTransactionsByCounterparty(counterparty: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByCounterparty(counterparty)
    
    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByDateRange(startDate, endDate)
    
    fun getTransactionsByDirection(direction: Transaction.TransactionDirection): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByDirection(direction)
    
    fun searchTransactions(query: String): Flow<List<Transaction>> = 
        transactionDao.searchTransactions(query)
    
    fun getTransactionsByLoanId(loanId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByLoanId(loanId)
    
    fun getTransactionsByTag(tag: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByTag(tag)
    
    suspend fun getTransactionById(id: String): Transaction? = transactionDao.getTransactionById(id)
    
    suspend fun insertTransaction(transaction: Transaction) {
        // Check for duplicates within 30 seconds
        val thirtySecondsAgo = transaction.dateTime.minusSeconds(30)
        val duplicateCount = transactionDao.checkDuplicateTransaction(
            transaction.amount,
            transaction.category,
            transaction.fromAccountId,
            thirtySecondsAgo,
            transaction.dateTime
        )
        
        if (duplicateCount > 0) {
            throw DuplicateTransactionException("Possible duplicate transaction detected")
        }
        
        // Update account balances
        when (transaction.direction) {
            Transaction.TransactionDirection.EXPENSE -> {
                accountDao.updateBalance(transaction.fromAccountId, -transaction.amount)
            }
            Transaction.TransactionDirection.INCOME -> {
                accountDao.updateBalance(transaction.fromAccountId, transaction.amount)
            }
            Transaction.TransactionDirection.TRANSFER -> {
                accountDao.updateBalance(transaction.fromAccountId, -transaction.amount)
                transaction.toAccountId?.let { toId ->
                    accountDao.updateBalance(toId, transaction.amount)
                }
            }
        }
        
        transactionDao.insertTransaction(transaction)
    }
    
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    
    suspend fun deleteTransaction(transaction: Transaction) {
        // Reverse the balance changes
        when (transaction.direction) {
            Transaction.TransactionDirection.EXPENSE -> {
                accountDao.updateBalance(transaction.fromAccountId, transaction.amount)
            }
            Transaction.TransactionDirection.INCOME -> {
                accountDao.updateBalance(transaction.fromAccountId, -transaction.amount)
            }
            Transaction.TransactionDirection.TRANSFER -> {
                accountDao.updateBalance(transaction.fromAccountId, transaction.amount)
                transaction.toAccountId?.let { toId ->
                    accountDao.updateBalance(toId, -transaction.amount)
                }
            }
        }
        
        transactionDao.deleteTransaction(transaction)
    }
    
    suspend fun deleteTransactionById(id: String) {
        getTransactionById(id)?.let { deleteTransaction(it) }
    }
    
    suspend fun getTotalIncome(startDate: LocalDateTime, endDate: LocalDateTime): Double = 
        transactionDao.getTotalIncome(startDate, endDate)
    
    suspend fun getTotalExpense(startDate: LocalDateTime, endDate: LocalDateTime): Double = 
        transactionDao.getTotalExpense(startDate, endDate)
    
    suspend fun getExpenseByCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<CategorySpending> = 
        transactionDao.getExpenseByCategory(startDate, endDate)
    
    suspend fun getBalanceOverTime(startDate: LocalDateTime, endDate: LocalDateTime): List<DailyBalance> = 
        transactionDao.getBalanceOverTime(startDate, endDate)
    
    fun getLoanSettlementTransactions(): Flow<List<Transaction>> = 
        transactionDao.getLoanSettlementTransactions()
    
    fun getTransactionsByLoanId(loanId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByLoanId(loanId)
}

class DuplicateTransactionException(message: String) : Exception(message)
