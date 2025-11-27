package com.example.pocketledger.data.repository

import com.example.pocketledger.data.dao.RecurringRuleDao
import com.example.pocketledger.data.model.RecurringRule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecurringRuleRepositoryImpl @Inject constructor(
    private val recurringRuleDao: RecurringRuleDao
) : RecurringRuleRepository {
    override fun getAllRules(): Flow<List<RecurringRule>> = recurringRuleDao.getAllRules()
    override suspend fun insertRule(rule: RecurringRule) = recurringRuleDao.insertRule(rule)
    override suspend fun updateRule(rule: RecurringRule) = recurringRuleDao.updateRule(rule)
    override suspend fun deleteRule(rule: RecurringRule) = recurringRuleDao.deleteRule(rule)
}
