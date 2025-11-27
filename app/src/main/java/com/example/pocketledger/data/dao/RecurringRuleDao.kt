package com.example.pocketledger.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pocketledger.data.model.RecurringRule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules ORDER BY nextDate ASC")
    fun getAllRecurringRules(): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_rules WHERE id = :id")
    suspend fun getRecurringRuleById(id: String): RecurringRule?

    @Query("SELECT * FROM recurring_rules WHERE isActive = 1 ORDER BY nextDate ASC")
    fun getActiveRecurringRules(): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_rules WHERE nextDate <= :date AND isActive = 1")
    suspend fun getDueRecurringRules(date: LocalDateTime = LocalDateTime.now()): List<RecurringRule>

    @Query("SELECT * FROM recurring_rules WHERE frequency = :frequency")
    fun getRecurringRulesByFrequency(frequency: RecurringRule.RecurrenceFrequency): Flow<List<RecurringRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringRule(rule: RecurringRule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringRules(rules: List<RecurringRule>)

    @Update
    suspend fun updateRecurringRule(rule: RecurringRule)

    @Delete
    suspend fun deleteRecurringRule(rule: RecurringRule)

    @Query("DELETE FROM recurring_rules WHERE id = :id")
    suspend fun deleteRecurringRuleById(id: String)

    @Query("UPDATE recurring_rules SET nextDate = :nextDate WHERE id = :ruleId")
    suspend fun updateNextDate(ruleId: String, nextDate: LocalDateTime)

    @Query("UPDATE recurring_rules SET isActive = :isActive WHERE id = :ruleId")
    suspend fun updateActiveStatus(ruleId: String, isActive: Boolean)

    @Query("UPDATE recurring_rules SET nextDate = :nextDate WHERE id = :ruleId AND isActive = 1")
    suspend fun updateNextDateIfActive(ruleId: String, nextDate: LocalDateTime)
}
