package com.example.pocketledger.di

import com.example.pocketledger.data.repository.AccountRepository
import com.example.pocketledger.data.repository.AccountRepositoryImpl
import com.example.pocketledger.data.repository.LoanRepository
import com.example.pocketledger.data.repository.LoanRepositoryImpl
import com.example.pocketledger.data.repository.RecurringRuleRepository
import com.example.pocketledger.data.repository.RecurringRuleRepositoryImpl
import com.example.pocketledger.data.repository.TransactionRepository
import com.example.pocketledger.data.repository.TransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    abstract fun bindLoanRepository(
        loanRepositoryImpl: LoanRepositoryImpl
    ): LoanRepository

    @Binds
    abstract fun bindRecurringRuleRepository(
        recurringRuleRepositoryImpl: RecurringRuleRepositoryImpl
    ): RecurringRuleRepository

    @Binds
    abstract fun bindBackupRepository(
        backupRepositoryImpl: com.example.pocketledger.data.repository.BackupRepositoryImpl
    ): com.example.pocketledger.data.repository.BackupRepository
}
