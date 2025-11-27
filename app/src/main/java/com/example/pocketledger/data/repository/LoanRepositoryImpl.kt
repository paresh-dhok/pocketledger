package com.example.pocketledger.data.repository

import com.example.pocketledger.data.dao.LoanDao
import com.example.pocketledger.data.model.LoanRecord
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val loanRecordDao: LoanDao
) : LoanRepository {
    override fun getAllLoans(): Flow<List<LoanRecord>> = loanRecordDao.getAllLoans()
    override fun getLoan(id: UUID): Flow<LoanRecord?> = loanRecordDao.getLoan(id)
    override suspend fun insertLoan(loan: LoanRecord) = loanRecordDao.insertLoan(loan)
    override suspend fun updateLoan(loan: LoanRecord) = loanRecordDao.updateLoan(loan)
    override suspend fun deleteLoan(loan: LoanRecord) = loanRecordDao.deleteLoan(loan)
}
