package com.example.pocketledger.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.pocketledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): Transaction?

    @Query("SELECT * FROM transactions WHERE fromAccountId = :accountId OR toAccountId = :accountId ORDER BY dateTime DESC")
    fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY dateTime DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE counterparty = :counterparty ORDER BY dateTime DESC")
    fun getTransactionsByCounterparty(counterparty: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE direction = :direction ORDER BY dateTime DESC")
    fun getTransactionsByDirection(direction: Transaction.TransactionDirection): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE tags LIKE '%' || :tag || '%' ORDER BY dateTime DESC")
    fun getTransactionsByTag(tag: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR counterparty LIKE '%' || :query || '%' ORDER BY dateTime DESC")
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isSettledLoan = 1 ORDER BY dateTime DESC")
    fun getLoanSettlementTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE relatedLoanId = :loanId ORDER BY dateTime DESC")
    fun getTransactionsByLoanId(loanId: String): Flow<List<Transaction>>

    @Query("SELECT COUNT(*) FROM transactions WHERE amount = :amount AND category = :category AND fromAccountId = :fromAccountId AND dateTime BETWEEN :startTime AND :endTime")
    suspend fun checkDuplicateTransaction(
        amount: Double,
        category: String,
        fromAccountId: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: String)

    @Query("SELECT SUM(amount) FROM transactions WHERE direction = 'INCOME' AND dateTime >= :startDate AND dateTime <= :endDate")
    suspend fun getTotalIncome(startDate: LocalDateTime, endDate: LocalDateTime): Double

    @Query("SELECT SUM(amount) FROM transactions WHERE direction = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate")
    suspend fun getTotalExpense(startDate: LocalDateTime, endDate: LocalDateTime): Double

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE direction = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate GROUP BY category ORDER BY total DESC")
    suspend fun getExpenseByCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<CategorySpending>

    @Query("SELECT dateTime, SUM(CASE WHEN direction = 'INCOME' THEN amount ELSE -amount END) as balance FROM transactions WHERE dateTime >= :startDate AND dateTime <= :endDate GROUP BY date(dateTime) ORDER BY dateTime")
    suspend fun getBalanceOverTime(startDate: LocalDateTime, endDate: LocalDateTime): List<DailyBalance>

    @RawQuery(observedEntities = [Transaction::class])
    fun getCustomTransactions(query: SupportSQLiteQuery): Flow<List<Transaction>>
}

data class CategorySpending(
    val category: String,
    val total: Double
)

data class DailyBalance(
    val dateTime: LocalDateTime,
    val balance: Double
)
