package com.example.pocketledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a financial transaction in the app.
 * @property id Unique identifier for the transaction
 * @property dateTime When the transaction occurred
 * @property amount Transaction amount (always positive)
 * @property direction Type of transaction (EXPENSE, INCOME, or TRANSFER)
 * @property fromAccountId Source account ID (required)
 * @property toAccountId Destination account ID (nullable for EXPENSE/INCOME)
 * @property category Transaction category (e.g., "Food", "Transport")
 * @param subcategory Optional subcategory
 * @param counterparty Who this transaction was with (person/shop name)
 * @param note Optional note about the transaction
 * @param tags List of tags for categorization
 * @param isSettledLoan Whether this transaction settles a loan
 * @param relatedLoanId If this is a loan transaction, reference to the loan
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["fromAccountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["fromAccountId"]),
        Index(value = ["toAccountId"]),
        Index(value = ["dateTime"], orders = [Index.Order.DESC]),
        Index(value = ["category"]),
        Index(value = ["direction"])
    ]
)
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val dateTime: LocalDateTime,
    val amount: Double,
    val direction: TransactionDirection,
    val fromAccountId: String,
    val toAccountId: String? = null,
    val category: String,
    val subcategory: String? = null,
    val counterparty: String? = null,
    val note: String? = null,
    val tags: List<String> = emptyList(),
    val isSettledLoan: Boolean = false,
    val relatedLoanId: String? = null
) {
    enum class TransactionDirection {
        EXPENSE,  // Money going out
        INCOME,   // Money coming in
        TRANSFER  // Money moving between accounts
    }
}
