package br.com.faleingles.data.repository

import br.com.faleingles.domain.model.ConversationMessage
import br.com.faleingles.domain.model.ConversationScenario
import br.com.faleingles.domain.model.MessageRole
import br.com.faleingles.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor() : ConversationRepository {

    override fun getScenarios(): Flow<List<ConversationScenario>> = flowOf(emptyList())

    override suspend fun sendMessage(
        scenarioId: String,
        history: List<ConversationMessage>,
        userMessage: String,
    ): ConversationMessage {
        // TODO: chamar API do backend que encaminha para Claude
        return ConversationMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.ASSISTANT,
            content = "Hello! I'm your AI tutor. (API integration coming soon)",
            timestampMillis = System.currentTimeMillis(),
        )
    }
}
