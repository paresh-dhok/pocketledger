package com.example.pocketledger.data.repository

import com.example.pocketledger.data.dao.TransactionDao
import com.example.pocketledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    override fun getTransactionsForAccount(accountId: UUID): Flow<List<Transaction>> = transactionDao.getTransactionsForAccount(accountId)
    override suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    override suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    override suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)
}
