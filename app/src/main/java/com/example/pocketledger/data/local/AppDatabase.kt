package com.example.pocketledger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pocketledger.data.local.dao.AccountDao
import com.example.pocketledger.data.local.dao.LoanRecordDao
import com.example.pocketledger.data.local.dao.RecurringRuleDao
import com.example.pocketledger.data.local.dao.TransactionDao
import com.example.pocketledger.data.local.entity.AccountEntity
import com.example.pocketledger.data.local.entity.LoanRecordEntity
import com.example.pocketledger.data.local.entity.RecurringRuleEntity
import com.example.pocketledger.data.local.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        LoanRecordEntity::class,
        RecurringRuleEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun loanRecordDao(): LoanRecordDao
    abstract fun recurringRuleDao(): RecurringRuleDao
}
