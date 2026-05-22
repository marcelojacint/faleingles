package br.com.faleingles.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import br.com.faleingles.BuildConfig
import br.com.faleingles.domain.auth.AuthRepository
import br.com.faleingles.domain.auth.AuthResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override suspend fun signInWithGoogle(context: Context): AuthResult {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user ?: return AuthResult.Error("Falha ao obter usuário")

            AuthResult.Success(user.uid, user.email)
        } catch (e: GetCredentialException) {
            AuthResult.Error("Login com Google cancelado")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Erro desconhecido ao fazer login com Google")
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Usuário não encontrado")
            AuthResult.Success(user.uid, user.email)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Erro ao criar conta")
            AuthResult.Success(user.uid, user.email)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    override suspend fun signOut() = firebaseAuth.signOut()

    override suspend fun getIdToken(): String? =
        firebaseAuth.currentUser?.getIdToken(false)?.await()?.token

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    private fun mapFirebaseError(message: String?): String = when {
        message == null -> "Erro desconhecido"
        "email address is already in use" in message -> "Este e-mail já está em uso"
        "password is invalid" in message || "wrong password" in message -> "Senha incorreta"
        "no user record" in message -> "Nenhuma conta com este e-mail"
        "badly formatted" in message -> "E-mail inválido"
        "weak password" in message -> "Senha muito fraca — use ao menos 6 caracteres"
        "network error" in message -> "Sem conexão com a internet"
        else -> "Erro de autenticação"
    }
}
