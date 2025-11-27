package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.dao.CategorySpending
import com.example.pocketledger.data.dao.DailyBalance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    
    fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>>
    
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>
    
    fun getTransactionsByCounterparty(counterparty: String): Flow<List<Transaction>>
    
    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>
    
    fun getTransactionsByDirection(direction: Transaction.TransactionDirection): Flow<List<Transaction>>
    
    fun searchTransactions(query: String): Flow<List<Transaction>>
    
    fun getTransactionsByLoanId(loanId: String): Flow<List<Transaction>>
    
    fun getTransactionsByTag(tag: String): Flow<List<Transaction>>
    
    suspend fun getTransactionById(id: String): Transaction?
    
    suspend fun insertTransaction(transaction: Transaction)
    
    suspend fun updateTransaction(transaction: Transaction)
    
    suspend fun deleteTransaction(transaction: Transaction)
    
    suspend fun deleteTransactionById(id: String)
    
    suspend fun getTotalIncome(startDate: LocalDateTime, endDate: LocalDateTime): Double
    
    suspend fun getTotalExpense(startDate: LocalDateTime, endDate: LocalDateTime): Double
    
    suspend fun getExpenseByCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<CategorySpending>
    
    suspend fun getBalanceOverTime(startDate: LocalDateTime, endDate: LocalDateTime): List<DailyBalance>
    
    fun getLoanSettlementTransactions(): Flow<List<Transaction>>
}

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: com.example.pocketledger.data.dao.TransactionDao,
    private val accountDao: com.example.pocketledger.data.dao.AccountDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    override fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByAccount(accountId)
    
    override fun getTransactionsByCategory(category: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByCategory(category)
    
    override fun getTransactionsByCounterparty(counterparty: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByCounterparty(counterparty)
    
    override fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByDateRange(startDate, endDate)
    
    override fun getTransactionsByDirection(direction: Transaction.TransactionDirection): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByDirection(direction)
    
    override fun searchTransactions(query: String): Flow<List<Transaction>> = 
        transactionDao.searchTransactions(query)
    
    override fun getTransactionsByLoanId(loanId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByLoanId(loanId)
    
    override fun getTransactionsByTag(tag: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByTag(tag)
    
    override suspend fun getTransactionById(id: String): Transaction? = transactionDao.getTransactionById(id)
    
    override suspend fun insertTransaction(transaction: Transaction) {
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
        
        // Balance updates are now handled by ProcessTransactionUseCase only
        // No manual balance updates here to avoid double accounting
        transactionDao.insertTransaction(transaction)
    }
    
    override suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    
    override suspend fun deleteTransaction(transaction: Transaction) {
        // Balance updates are now handled by ProcessTransactionUseCase only
        // No manual balance updates here to avoid double accounting
        transactionDao.deleteTransaction(transaction)
    }
    
    override suspend fun deleteTransactionById(id: String) {
        getTransactionById(id)?.let { deleteTransaction(it) }
    }
    
    override suspend fun getTotalIncome(startDate: LocalDateTime, endDate: LocalDateTime): Double = 
        transactionDao.getTotalIncome(startDate, endDate)
    
    override suspend fun getTotalExpense(startDate: LocalDateTime, endDate: LocalDateTime): Double = 
        transactionDao.getTotalExpense(startDate, endDate)
    
    override suspend fun getExpenseByCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<CategorySpending> = 
        transactionDao.getExpenseByCategory(startDate, endDate)
    
    override suspend fun getBalanceOverTime(startDate: LocalDateTime, endDate: LocalDateTime): List<DailyBalance> = 
        transactionDao.getBalanceOverTime(startDate, endDate)
    
    override fun getLoanSettlementTransactions(): Flow<List<Transaction>> = 
        transactionDao.getLoanSettlementTransactions()
}

class DuplicateTransactionException(message: String) : Exception(message)
