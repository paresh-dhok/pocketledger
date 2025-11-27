package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    
    fun getAccountsByType(type: Account.AccountType): Flow<List<Account>>
    
    suspend fun getAccountById(id: String): Account?
    
    suspend fun insertAccount(account: Account)
    
    suspend fun updateAccount(account: Account)
    
    suspend fun deleteAccount(account: Account)
    
    suspend fun deleteAccountById(id: String)
    
    suspend fun getTotalBalance(): Double
    
    suspend fun updateBalance(accountId: String, amount: Double)
    
    suspend fun getTransactionCount(accountId: String): Int
    
    suspend fun canDeleteAccount(accountId: String): Boolean
}

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: com.example.pocketledger.data.dao.AccountDao
) : AccountRepository {
    override fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()
    
    override fun getAccountsByType(type: Account.AccountType): Flow<List<Account>> = 
        accountDao.getAccountsByType(type)
    
    override suspend fun getAccountById(id: String): Account? = accountDao.getAccountById(id)
    
    override suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)
    
    override suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
    
    override suspend fun deleteAccount(account: Account) = accountDao.deleteAccount(account)
    
    override suspend fun deleteAccountById(id: String) = accountDao.deleteAccountById(id)
    
    override suspend fun getTotalBalance(): Double = accountDao.getTotalBalance()
    
    override suspend fun updateBalance(accountId: String, amount: Double) = 
        accountDao.updateBalance(accountId, amount)
    
    override suspend fun getTransactionCount(accountId: String): Int = 
        accountDao.getTransactionCount(accountId)
    
    override suspend fun canDeleteAccount(accountId: String): Boolean {
        return getTransactionCount(accountId) == 0
    }
}
