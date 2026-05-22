package br.com.faleingles.domain.repository

import br.com.faleingles.domain.model.ConversationMessage
import br.com.faleingles.domain.model.ConversationScenario
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getScenarios(): Flow<List<ConversationScenario>>
    suspend fun sendMessage(
        scenarioId: String,
        history: List<ConversationMessage>,
        userMessage: String,
    ): ConversationMessage
}
