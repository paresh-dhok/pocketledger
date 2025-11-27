package com.example.pocketledger.di

import android.content.Context
import androidx.room.Room
import com.example.pocketledger.data.database.AppDatabase
import com.example.pocketledger.data.dao.AccountDao
import com.example.pocketledger.data.dao.LoanDao
import com.example.pocketledger.data.dao.RecurringRuleDao
import com.example.pocketledger.data.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Initialize SQLCipher with a secure passphrase
        // In production, this should be managed through Android Keystore
        val passphrase = SQLiteDatabase.getBytes("pocketledger-secure-key-2024".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pocket_ledger_database"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideLoanDao(database: AppDatabase): LoanDao = database.loanDao()

    @Provides
    fun provideRecurringRuleDao(database: AppDatabase): RecurringRuleDao = database.recurringRuleDao()
}
