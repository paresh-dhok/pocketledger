package com.example.pocketledger.data.repository

import com.example.pocketledger.data.local.dao.AccountDao
import com.example.pocketledger.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {
    override fun getAllAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    override fun getAccount(id: UUID): Flow<AccountEntity?> = accountDao.getAccount(id)
    override suspend fun getAccountById(id: UUID): AccountEntity? = accountDao.getAccountById(id)
    override suspend fun insertAccount(account: AccountEntity) = accountDao.insertAccount(account)
    override suspend fun updateAccount(account: AccountEntity) = accountDao.updateAccount(account)
    override suspend fun deleteAccount(account: AccountEntity) = accountDao.deleteAccount(account)
}
