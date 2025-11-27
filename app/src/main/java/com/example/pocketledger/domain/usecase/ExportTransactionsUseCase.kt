package com.example.pocketledger.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.pocketledger.data.repository.TransactionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExportTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val transactions = transactionRepository.getAllTransactions().first()
            val csvBuilder = StringBuilder()
            csvBuilder.append("ID,Date,Amount,Direction,Category,Note\n")
            
            val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

            transactions.forEach { t ->
                csvBuilder.append(
                    "${t.id},${t.datetime.format(dateFormatter)},${t.amount},${t.direction},${t.category},\"${t.note ?: ""}\"\n"
                )
            }

            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(csvBuilder.toString().toByteArray())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
