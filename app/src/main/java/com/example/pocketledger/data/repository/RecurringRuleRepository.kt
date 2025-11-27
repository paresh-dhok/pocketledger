package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.RecurringRule
import com.example.pocketledger.data.dao.RecurringRuleDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface RecurringRuleRepository {
    fun getAllRules(): Flow<List<RecurringRule>>

    suspend fun insertRule(rule: RecurringRule)

    suspend fun updateRule(rule: RecurringRule)

    suspend fun deleteRule(rule: RecurringRule)

    suspend fun getRuleById(id: String): RecurringRule?
}

@Singleton
class RecurringRuleRepositoryImpl @Inject constructor(
    private val recurringRuleDao: RecurringRuleDao
) : RecurringRuleRepository {
    override fun getAllRules(): Flow<List<RecurringRule>> {
        return recurringRuleDao.getAllRules()
    }

    override suspend fun insertRule(rule: RecurringRule) {
        recurringRuleDao.insertRule(rule)
    }

    override suspend fun updateRule(rule: RecurringRule) {
        recurringRuleDao.updateRule(rule)
    }

    override suspend fun deleteRule(rule: RecurringRule) {
        recurringRuleDao.deleteRule(rule)
    }

    override suspend fun getRuleById(id: String): RecurringRule? {
        return recurringRuleDao.getRuleById(id)
    }
}
