package com.example.pocketledger.data.repository

import com.example.pocketledger.data.local.dao.RecurringRuleDao
import com.example.pocketledger.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecurringRuleRepositoryImpl @Inject constructor(
    private val recurringRuleDao: RecurringRuleDao
) : RecurringRuleRepository {
    override fun getAllRules(): Flow<List<RecurringRuleEntity>> = recurringRuleDao.getAllRules()
    override suspend fun insertRule(rule: RecurringRuleEntity) = recurringRuleDao.insertRule(rule)
    override suspend fun updateRule(rule: RecurringRuleEntity) = recurringRuleDao.updateRule(rule)
    override suspend fun deleteRule(rule: RecurringRuleEntity) = recurringRuleDao.deleteRule(rule)
}
