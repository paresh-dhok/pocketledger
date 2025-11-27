package com.example.pocketledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a loan record between the user and another party.
 * @property id Unique identifier for the loan
 * @property lenderOrBorrower Whether the user is the lender or borrower
 * @property counterparty The other party involved in the loan
 * @property originalAmount The original loan amount
 * @property outstandingAmount Remaining amount to be paid
 * @property createdAt When the loan was created
 * @property history List of transaction IDs related to this loan
 */
@Entity(
    tableName = "loans",
    indices = [
        Index(value = ["createdAt"], orders = [Index.Order.DESC])
    ]
)
data class LoanRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val lenderOrBorrower: LoanType,
    val counterparty: String,
    val originalAmount: Double,
    val outstandingAmount: Double,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val history: List<String> = emptyList()
) {
    enum class LoanType {
        I_LENT,     // User is the lender
        I_BORROWED  // User is the borrower
    }
}
