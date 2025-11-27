package com.example.pocketledger.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface BackupRepository {
    suspend fun createBackup(uri: Uri): Result<Unit>
    suspend fun restoreBackup(uri: Uri): Result<Unit>
    fun getLastBackupTime(): Flow<Long?>
}
