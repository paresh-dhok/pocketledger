package com.example.pocketledger.data.repository

import com.example.pocketledger.data.model.LoanRecord
import com.example.pocketledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoanRepository @Inject constructor(
    private val loanDao: com.example.pocketledger.data.dao.LoanDao,
    private val transactionDao: com.example.pocketledger.data.dao.TransactionDao
) {
    fun getAllLoans(): Flow<List<LoanRecord>> = loanDao.getAllLoans()
    
    fun getActiveLoans(): Flow<List<LoanRecord>> = loanDao.getActiveLoans()
    
    fun getLoansByType(type: LoanRecord.LoanType): Flow<List<LoanRecord>> = 
        loanDao.getLoansByType(type)
    
    fun getLoansByCounterparty(counterparty: String): Flow<List<LoanRecord>> = 
        loanDao.getLoansByCounterparty(counterparty)
    
    suspend fun getLoanById(id: String): LoanRecord? {
        return loanDao.getLoanById(id)
    }
    
    suspend fun insertLoan(loan: LoanRecord) = loanDao.insertLoan(loan)
    
    suspend fun updateLoan(loan: LoanRecord) = loanDao.updateLoan(loan)
    
    suspend fun deleteLoan(loan: LoanRecord) {
        loanDao.deleteLoan(loan)
    }
    
    suspend fun deleteLoanById(loanId: String) {
        val loan = getLoanById(loanId) ?: return
        deleteLoan(loan)
    }
    
    suspend fun makeLoanPayment(loanId: String, paymentAmount: Double, paymentTransaction: Transaction) {
        val loan = getLoanById(loanId) ?: throw IllegalArgumentException("Loan not found")
        
        if (paymentAmount > loan.outstandingAmount) {
            throw IllegalArgumentException("Payment amount exceeds outstanding amount")
        }
        
        // Update outstanding amount
        val newOutstanding = loan.outstandingAmount - paymentAmount
        loanDao.updateOutstandingAmount(loanId, newOutstanding)
        
        // Add transaction to loan history
        val updatedHistory = loan.history + paymentTransaction.id
        val updatedLoan = loan.copy(
            outstandingAmount = newOutstanding,
            history = updatedHistory
        )
        loanDao.updateLoan(updatedLoan)
        
        // Mark transaction as loan settlement
        val updatedTransaction = paymentTransaction.copy(
            isSettledLoan = true,
            relatedLoanId = loanId
        )
        transactionDao.updateTransaction(updatedTransaction)
    }
    
    suspend fun updateOutstandingAmount(loanId: String, newAmount: Double) {
        val loan = getLoanById(loanId) ?: return
        val updatedLoan = loan.copy(outstandingAmount = newAmount)
        updateLoan(updatedLoan)
    }
    
    suspend fun getTotalOutstandingByType(type: LoanRecord.LoanType): Double = 
        loanDao.getTotalOutstandingByType(type)
    
    suspend fun createLoan(
        counterparty: String,
        amount: Double,
        type: LoanRecord.LoanType,
        relatedTransactionId: String? = null
    ): LoanRecord {
        val loan = LoanRecord(
            counterparty = counterparty,
            originalAmount = amount,
            outstandingAmount = amount,
            lenderOrBorrower = type,
            history = relatedTransactionId?.let { listOf(it) } ?: emptyList()
        )
        loanDao.insertLoan(loan)
        return loan
    }
    
        
    suspend fun settleLoan(loanId: String): Transaction? {
        val loan = getLoanById(loanId) ?: throw IllegalArgumentException("Loan not found")
        
        if (loan.outstandingAmount <= 0) {
            return null // Loan already settled
        }
        
        val settlementTransaction = Transaction(
            dateTime = LocalDateTime.now(),
            amount = loan.outstandingAmount,
            direction = when (loan.lenderOrBorrower) {
                LoanRecord.LoanType.I_LENT -> Transaction.TransactionDirection.INCOME
                LoanRecord.LoanType.I_BORROWED -> Transaction.TransactionDirection.EXPENSE
            },
            fromAccountId = "", // This should be set by the caller
            category = "Loan Settlement",
            counterparty = loan.counterparty,
            isSettledLoan = true,
            relatedLoanId = loanId
        )
        
        return settlementTransaction
    }
    
    fun getTransactionsByLoanId(loanId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByLoanId(loanId)
}
