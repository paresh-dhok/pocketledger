package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: com.example.pocketledger.data.dao.AccountDao
) {
    fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()
    
    fun getAccountsByType(type: Account.AccountType): Flow<List<Account>> = 
        accountDao.getAccountsByType(type)
    
    suspend fun getAccountById(id: String): Account? = accountDao.getAccountById(id)
    
    suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)
    
    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
    
    suspend fun deleteAccount(account: Account) = accountDao.deleteAccount(account)
    
    suspend fun deleteAccountById(id: String) = accountDao.deleteAccountById(id)
    
    suspend fun getTotalBalance(): Double = accountDao.getTotalBalance()
    
    suspend fun updateBalance(accountId: String, amount: Double) = 
        accountDao.updateBalance(accountId, amount)
    
    suspend fun getTransactionCount(accountId: String): Int = 
        accountDao.getTransactionCount(accountId)
    
    suspend fun canDeleteAccount(accountId: String): Boolean {
        return getTransactionCount(accountId) == 0
    }
}
