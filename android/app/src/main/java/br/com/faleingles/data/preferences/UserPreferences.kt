package br.com.faleingles.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val IS_PRO = booleanPreferencesKey("is_pro")
        val DAILY_GOAL = intPreferencesKey("daily_goal_minutes")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    val userId: Flow<String?> = context.dataStore.data.map { it[Keys.USER_ID] }
    val isPro: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_PRO] ?: false }
    val dailyGoal: Flow<Int> = context.dataStore.data.map { it[Keys.DAILY_GOAL] ?: 10 }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val authToken: Flow<String?> = context.dataStore.data.map { it[Keys.AUTH_TOKEN] }

    suspend fun userIdSnapshot(): String? = userId.first()
    suspend fun isProSnapshot(): Boolean = isPro.first()
    suspend fun authTokenSnapshot(): String? = authToken.first()

    suspend fun saveUserId(id: String) = context.dataStore.edit { it[Keys.USER_ID] = id }
    suspend fun saveIsPro(pro: Boolean) = context.dataStore.edit { it[Keys.IS_PRO] = pro }
    suspend fun saveDailyGoal(minutes: Int) = context.dataStore.edit { it[Keys.DAILY_GOAL] = minutes }
    suspend fun markOnboardingDone() = context.dataStore.edit { it[Keys.ONBOARDING_DONE] = true }
    suspend fun saveAuthToken(token: String) = context.dataStore.edit { it[Keys.AUTH_TOKEN] = token }
    suspend fun clearAuth() = context.dataStore.edit {
        it.remove(Keys.USER_ID)
        it.remove(Keys.AUTH_TOKEN)
        it.remove(Keys.IS_PRO)
    }
}
