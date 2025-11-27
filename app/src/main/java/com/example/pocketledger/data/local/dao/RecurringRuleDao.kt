package com.example.pocketledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pocketledger.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules")
    fun getAllRules(): Flow<List<RecurringRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RecurringRuleEntity)

    @Update
    suspend fun updateRule(rule: RecurringRuleEntity)

    @Delete
    suspend fun deleteRule(rule: RecurringRuleEntity)
}
