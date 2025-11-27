package com.example.pocketledger.data.repository

import com.example.pocketledger.data.dao.AccountDao
import com.example.pocketledger.data.model.Account
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {
    override fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()
    override fun getAccount(id: UUID): Flow<Account?> = accountDao.getAccount(id)
    override suspend fun getAccountById(id: UUID): Account? = accountDao.getAccountById(id)
    override suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)
    override suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
    override suspend fun deleteAccount(account: Account) = accountDao.deleteAccount(account)
}
