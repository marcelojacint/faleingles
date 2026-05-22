package br.com.faleingles.domain.auth

import android.content.Context

interface AuthRepository {
    suspend fun signInWithGoogle(context: Context): AuthResult
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signUpWithEmail(email: String, password: String): AuthResult
    suspend fun signOut()
    suspend fun getIdToken(): String?
    fun getCurrentUserId(): String?
    fun isSignedIn(): Boolean
}

sealed interface AuthResult {
    data class Success(val userId: String, val email: String?) : AuthResult
    data class Error(val message: String) : AuthResult
}
