package com.example.pocketledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents a financial account in the app.
 * @property id Unique identifier for the account
 * @property name Display name of the account (e.g., "HDFC", "Cash")
 * @property type Type of the account (CASH or BANK)
 * @property balance Current balance in the account
 * @property currency Currency code (default: "INR")
 */
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: String = "INR"
) {
    enum class AccountType {
        CASH,
        BANK
    }
}
