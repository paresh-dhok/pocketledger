package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.RecurringRule
import com.example.pocketledger.data.dao.RecurringRuleDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringRuleRepository @Inject constructor(
    private val recurringRuleDao: RecurringRuleDao
) {
    fun getAllRules(): Flow<List<RecurringRule>> {
        return recurringRuleDao.getAllRules()
    }

    suspend fun insertRule(rule: RecurringRule) {
        recurringRuleDao.insertRule(rule)
    }

    suspend fun updateRule(rule: RecurringRule) {
        recurringRuleDao.updateRule(rule)
    }

    suspend fun deleteRule(rule: RecurringRule) {
        recurringRuleDao.deleteRule(rule)
    }

    suspend fun getRuleById(id: String): RecurringRule? {
        return recurringRuleDao.getRuleById(id)
    }
}
