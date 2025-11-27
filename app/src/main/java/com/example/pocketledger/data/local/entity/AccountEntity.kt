package com.example.pocketledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.UUID

enum class AccountType { CASH, BANK }

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val type: AccountType,
    val balance: BigDecimal,
    val currency: String = "INR"
)
