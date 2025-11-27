package com.example.pocketledger.data.repository

import com.example.pocketledger.data.local.dao.LoanRecordDao
import com.example.pocketledger.data.local.entity.LoanRecordEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val loanRecordDao: LoanRecordDao
) : LoanRepository {
    override fun getAllLoans(): Flow<List<LoanRecordEntity>> = loanRecordDao.getAllLoans()
    override fun getLoan(id: UUID): Flow<LoanRecordEntity?> = loanRecordDao.getLoan(id)
    override suspend fun insertLoan(loan: LoanRecordEntity) = loanRecordDao.insertLoan(loan)
    override suspend fun updateLoan(loan: LoanRecordEntity) = loanRecordDao.updateLoan(loan)
    override suspend fun deleteLoan(loan: LoanRecordEntity) = loanRecordDao.deleteLoan(loan)
}
