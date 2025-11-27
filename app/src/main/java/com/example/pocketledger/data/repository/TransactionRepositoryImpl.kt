package com.example.pocketledger.data.repository

import com.example.pocketledger.data.local.dao.TransactionDao
import com.example.pocketledger.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    override fun getTransactionsForAccount(accountId: UUID): Flow<List<TransactionEntity>> = transactionDao.getTransactionsForAccount(accountId)
    override suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    override suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    override suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)
}
