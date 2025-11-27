package com.example.pocketledger.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages encryption keys using Android Keystore for secure database encryption.
 */
@Singleton
class KeystoreManager @Inject constructor() {
    
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * Gets or creates the database encryption key.
     * @return The database passphrase as a ByteArray
     */
    fun getDatabaseKey(): ByteArray {
        // Check if key exists
        if (!keyStore.containsAlias(DB_KEY_ALIAS)) {
            generateKey()
        }
        
        // For SQLCipher, we need a passphrase string
        // Generate a secure random passphrase and encrypt it
        return  getOrCreatePassphrase()
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            DB_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // Set to true for biometric/PIN protection
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getOrCreatePassphrase(): ByteArray {
        val prefs = android.app.Application().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        
        // Check if encrypted passphrase exists
        val encryptedPassphrase = prefs.getString(ENCRYPTED_PASSPHRASE_KEY, null)
        
        return if (encryptedPassphrase != null) {
            // Decrypt existing passphrase
            val parts = encryptedPassphrase.split(":")
            val iv = android.util.Base64.decode(parts[0], android.util.Base64.DEFAULT)
            val encrypted = android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT)
            decrypt(encrypted, iv)
        } else {
            // Generate new random passphrase
            val passphrase = generateRandomPassphrase()
            val (encrypted, iv) = encrypt(passphrase)
            
            // Store encrypted passphrase
            val combined = "${android.util.Base64.encodeToString(iv, android.util.Base64.DEFAULT)}:" +
                          "${android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)}"
            prefs.edit().putString(ENCRYPTED_PASSPHRASE_KEY, combined).apply()
            
            passphrase
        }
    }

    private fun generateRandomPassphrase(): ByteArray {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32) // 256-bit passphrase
        random.nextBytes(bytes)
        return bytes
    }

    private fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        val key = keyStore.getKey(DB_KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)
        return Pair(encrypted, iv)
    }

    private fun decrypt(encrypted: ByteArray, iv: ByteArray): ByteArray {
        val key = keyStore.getKey(DB_KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encrypted)
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val DB_KEY_ALIAS = "pocketledger_db_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val PREFS_NAME = "pocketledger_secure_prefs"
        private const val ENCRYPTED_PASSPHRASE_KEY = "encrypted_db_passphrase"
    }
}
