package com.example.pocketledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

enum class TransactionDirection { EXPENSE, INCOME, TRANSFER }

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val datetime: LocalDateTime,
    val amount: BigDecimal,
    val direction: TransactionDirection,
    val fromAccountId: UUID,
    val toAccountId: UUID?,
    val category: String,
    val subcategory: String?,
    val counterparty: String?,
    val note: String?,
    val tags: List<String>,
    val isSettledLoan: Boolean,
    val relatedLoanId: UUID?
)
