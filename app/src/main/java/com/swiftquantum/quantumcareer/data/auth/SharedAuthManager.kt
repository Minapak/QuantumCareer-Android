package com.swiftquantum.quantumcareer.data.auth

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authentication data class containing all user auth information
 */
data class AuthData(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
    val userEmail: String? = null,
    val userName: String? = null,
    val loginTimestamp: Long = 0L,
    val isLoggedIn: Boolean = false
)

/**
 * SharedAuthManager - Manages cross-app authentication using UnifiedAuthProvider
 *
 * This manager provides a simple interface to save, retrieve, and clear authentication
 * data that is shared across all SwiftQuantum apps via the ContentProvider hosted by SwiftQuantum.
 */
@Singleton
class SharedAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val AUTHORITY = "com.swiftquantum.auth.provider"
        private val AUTH_URI: Uri = Uri.parse("content://$AUTHORITY/auth")

        // Column names matching the provider
        const val COLUMN_ACCESS_TOKEN = "access_token"
        const val COLUMN_REFRESH_TOKEN = "refresh_token"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_EMAIL = "user_email"
        const val COLUMN_USER_NAME = "user_name"
        const val COLUMN_LOGIN_TIMESTAMP = "login_timestamp"
        const val COLUMN_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Saves authentication data to the shared provider
     */
    fun saveAuth(
        accessToken: String,
        refreshToken: String,
        userId: String,
        userEmail: String,
        userName: String
    ) {
        val values = ContentValues().apply {
            put(COLUMN_ACCESS_TOKEN, accessToken)
            put(COLUMN_REFRESH_TOKEN, refreshToken)
            put(COLUMN_USER_ID, userId)
            put(COLUMN_USER_EMAIL, userEmail)
            put(COLUMN_USER_NAME, userName)
        }

        try {
            val rowsUpdated = context.contentResolver.update(AUTH_URI, values, null, null)
            if (rowsUpdated == 0) {
                context.contentResolver.insert(AUTH_URI, values)
            }
        } catch (e: Exception) {
            try {
                context.contentResolver.insert(AUTH_URI, values)
            } catch (e2: Exception) {
                // Provider not available
            }
        }
    }

    /**
     * Retrieves authentication data from the shared provider
     */
    fun getAuth(): AuthData {
        return try {
            context.contentResolver.query(
                AUTH_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val accessTokenIndex = cursor.getColumnIndex(COLUMN_ACCESS_TOKEN)
                    val refreshTokenIndex = cursor.getColumnIndex(COLUMN_REFRESH_TOKEN)
                    val userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID)
                    val userEmailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL)
                    val userNameIndex = cursor.getColumnIndex(COLUMN_USER_NAME)
                    val timestampIndex = cursor.getColumnIndex(COLUMN_LOGIN_TIMESTAMP)
                    val isLoggedInIndex = cursor.getColumnIndex(COLUMN_IS_LOGGED_IN)

                    AuthData(
                        accessToken = if (accessTokenIndex >= 0) cursor.getString(accessTokenIndex) else null,
                        refreshToken = if (refreshTokenIndex >= 0) cursor.getString(refreshTokenIndex) else null,
                        userId = if (userIdIndex >= 0) cursor.getString(userIdIndex) else null,
                        userEmail = if (userEmailIndex >= 0) cursor.getString(userEmailIndex) else null,
                        userName = if (userNameIndex >= 0) cursor.getString(userNameIndex) else null,
                        loginTimestamp = if (timestampIndex >= 0) cursor.getLong(timestampIndex) else 0L,
                        isLoggedIn = if (isLoggedInIndex >= 0) cursor.getInt(isLoggedInIndex) == 1 else false
                    )
                } else {
                    AuthData()
                }
            } ?: AuthData()
        } catch (e: Exception) {
            AuthData()
        }
    }

    /**
     * Clears all authentication data (logout)
     */
    fun clearAuth() {
        try {
            context.contentResolver.delete(AUTH_URI, null, null)
        } catch (e: Exception) {
            // Silent fail
        }
    }

    /**
     * Checks if user is currently logged in
     */
    fun isLoggedIn(): Boolean {
        return getAuth().isLoggedIn
    }

    /**
     * Gets the current access token
     */
    fun getAccessToken(): String? {
        return getAuth().accessToken
    }

    /**
     * Updates just the access token (for token refresh)
     */
    fun updateAccessToken(newAccessToken: String) {
        val currentAuth = getAuth()
        if (currentAuth.isLoggedIn) {
            val values = ContentValues().apply {
                put(COLUMN_ACCESS_TOKEN, newAccessToken)
            }
            try {
                context.contentResolver.update(AUTH_URI, values, null, null)
            } catch (e: Exception) {
                // Provider not available
            }
        }
    }

    /**
     * Observes authentication state changes as a Flow
     */
    fun observeAuthState(): Flow<AuthData> = callbackFlow {
        trySend(getAuth())

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(getAuth())
            }

            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySend(getAuth())
            }
        }

        try {
            context.contentResolver.registerContentObserver(
                AUTH_URI,
                true,
                observer
            )
        } catch (e: Exception) {
            // Provider not available
        }

        awaitClose {
            try {
                context.contentResolver.unregisterContentObserver(observer)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }.distinctUntilChanged()
}
