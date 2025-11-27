package com.example.pocketledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

enum class LoanType { I_LENT, I_BORROWED }

@Entity(tableName = "loan_records")
data class LoanRecordEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val lenderOrBorrower: LoanType,
    val counterparty: String,
    val originalAmount: BigDecimal,
    val outstandingAmount: BigDecimal,
    val createdAt: LocalDateTime,
    val history: List<UUID> // List of transaction IDs
)
