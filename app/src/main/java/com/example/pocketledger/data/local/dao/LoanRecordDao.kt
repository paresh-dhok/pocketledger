package com.example.pocketledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pocketledger.data.local.entity.LoanRecordEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface LoanRecordDao {
    @Query("SELECT * FROM loan_records")
    fun getAllLoans(): Flow<List<LoanRecordEntity>>

    @Query("SELECT * FROM loan_records WHERE id = :id")
    fun getLoan(id: UUID): Flow<LoanRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanRecordEntity)

    @Update
    suspend fun updateLoan(loan: LoanRecordEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanRecordEntity)
}
