package com.example.pocketledger.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pocketledger.data.model.LoanRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY createdAt DESC")
    fun getAllLoans(): Flow<List<LoanRecord>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: String): LoanRecord?

    @Query("SELECT * FROM loans WHERE lenderOrBorrower = :type ORDER BY createdAt DESC")
    fun getLoansByType(type: LoanRecord.LoanType): Flow<List<LoanRecord>>

    @Query("SELECT * FROM loans WHERE outstandingAmount > 0 ORDER BY createdAt DESC")
    fun getActiveLoans(): Flow<List<LoanRecord>>

    @Query("SELECT * FROM loans WHERE counterparty = :counterparty ORDER BY createdAt DESC")
    fun getLoansByCounterparty(counterparty: String): Flow<List<LoanRecord>>

    @Query("SELECT SUM(outstandingAmount) FROM loans WHERE lenderOrBorrower = :type AND outstandingAmount > 0")
    suspend fun getTotalOutstandingByType(type: LoanRecord.LoanType): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<LoanRecord>)

    @Update
    suspend fun updateLoan(loan: LoanRecord)

    @Delete
    suspend fun deleteLoan(loan: LoanRecord)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoanById(id: String)

    @Query("UPDATE loans SET outstandingAmount = outstandingAmount - :amount WHERE id = :loanId")
    suspend fun reduceOutstandingAmount(loanId: String, amount: Double)

    @Query("UPDATE loans SET outstandingAmount = outstandingAmount + :amount WHERE id = :loanId")
    suspend fun increaseOutstandingAmount(loanId: String, amount: Double)

    @Query("UPDATE loans SET outstandingAmount = :newAmount WHERE id = :loanId")
    suspend fun updateOutstandingAmount(loanId: String, newAmount: Double)
}
