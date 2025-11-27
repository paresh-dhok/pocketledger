package com.example.pocketledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

enum class RecurringFrequency { DAILY, WEEKLY, MONTHLY }

@Entity(tableName = "recurring_rules")
data class RecurringRuleEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val amount: BigDecimal,
    val direction: TransactionDirection,
    val fromAccountId: UUID,
    val toAccountId: UUID?,
    val category: String,
    val frequency: RecurringFrequency,
    val nextDate: LocalDateTime
)
