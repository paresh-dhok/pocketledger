package com.example.pocketledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a rule for recurring transactions.
 * @property id Unique identifier for the rule
 * @property transactionTemplate Partial transaction template to use when creating new instances
 * @property frequency How often the transaction should recur
 * @property nextDate When the next instance should be created
 */
@Entity(tableName = "recurring_rules")
data class RecurringRule(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val transactionTemplate: TransactionTemplate,
    val frequency: RecurrenceFrequency,
    val nextDate: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val isActive: Boolean = true
) {
    enum class RecurrenceFrequency {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }

    /**
     * Simplified transaction template that can be converted to a full Transaction
     */
    data class TransactionTemplate(
        val amount: Double,
        val direction: Transaction.TransactionDirection,
        val fromAccountId: String,
        val toAccountId: String? = null,
        val category: String,
        val subcategory: String? = null,
        val counterparty: String? = null,
        val note: String? = null,
        val tags: List<String> = emptyList()
    )
}
