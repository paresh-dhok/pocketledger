package com.example.pocketledger.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.pocketledger.data.dao.AccountDao
import com.example.pocketledger.data.dao.LoanDao
import com.example.pocketledger.data.dao.RecurringRuleDao
import com.example.pocketledger.data.dao.TransactionDao
import com.example.pocketledger.data.model.Account
import com.example.pocketledger.data.model.LoanRecord
import com.example.pocketledger.data.model.RecurringRule
import com.example.pocketledger.data.model.Transaction
import com.example.pocketledger.data.database.converters.DateConverters
import com.example.pocketledger.data.database.converters.ListConverters
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportFactory

@Database(
    entities = [
        Account::class,
        Transaction::class,
        LoanRecord::class,
        RecurringRule::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    DateConverters::class,
    ListConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun loanDao(): LoanDao
    abstract fun recurringRuleDao(): RecurringRuleDao


}
