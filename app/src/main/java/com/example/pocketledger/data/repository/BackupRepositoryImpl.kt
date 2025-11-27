package com.example.pocketledger.data.repository

import android.content.Context
import android.net.Uri
import com.example.pocketledger.data.database.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) : BackupRepository {

    override suspend fun createBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath("pocketledger.db")
            if (!dbFile.exists()) {
                return@withContext Result.failure(Exception("Database not found"))
            }

            // Checkpoint to ensure all data is written to the main file
            database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)")

            context.contentResolver.openOutputStream(uri)?.use { output ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(output)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath("pocketledger.db")
            
            // Close database before restoring? Room doesn't expose close easily without closing the app.
            // In a real app, we might need to restart the process or ensure no active connections.
            // For this simple implementation, we'll try to overwrite.
            // Ideally, we should close the DB, overwrite, and then reopen.
            
            if (database.isOpen) {
                database.close()
            }

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLastBackupTime(): Flow<Long?> = flow {
        // TODO: Implement storing/retrieving last backup time from Preferences
        emit(null)
    }
}
