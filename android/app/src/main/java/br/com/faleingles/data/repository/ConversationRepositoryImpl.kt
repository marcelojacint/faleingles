package br.com.faleingles.data.repository

import br.com.faleingles.data.remote.api.FaleInglesApi
import br.com.faleingles.data.remote.dto.MessageDto
import br.com.faleingles.data.remote.dto.SendMessageRequestDto
import br.com.faleingles.domain.model.ConversationMessage
import br.com.faleingles.domain.model.ConversationScenario
import br.com.faleingles.domain.model.CorrectionType
import br.com.faleingles.domain.model.GrammarCorrection
import br.com.faleingles.domain.model.MessageRole
import br.com.faleingles.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val api: FaleInglesApi,
) : ConversationRepository {

    override fun getScenarios(): Flow<List<ConversationScenario>> = flowOf(
        listOf(
            ConversationScenario(
                id = "free_conversation",
                title = "Conversa Livre",
                description = "Fale sobre qualquer assunto",
                systemPrompt = "Have a natural English conversation. Adapt complexity to the user's level.",
                requiredPhase = 1,
            ),
            ConversationScenario(
                id = "job_interview",
                title = "Entrevista de Emprego",
                description = "Simule uma entrevista em inglês",
                systemPrompt = "You are a recruiter conducting a job interview in English. Ask common interview questions.",
                requiredPhase = 3,
            ),
            ConversationScenario(
                id = "ordering_coffee",
                title = "Pedindo um café",
                description = "Numa cafeteria americana",
                systemPrompt = "You are a barista at a coffee shop in New York. The customer wants to order.",
                requiredPhase = 1,
            ),
        )
    )

    override suspend fun sendMessage(
        scenarioId: String,
        history: List<ConversationMessage>,
        userMessage: String,
    ): ConversationMessage {
        val scenario = getScenarios().let { flow ->
            var found: ConversationScenario? = null
            flow.collect { found = it.firstOrNull { s -> s.id == scenarioId } }
            found
        } ?: return localFallback(userMessage)

        return try {
            val response = api.sendConversationMessage(
                SendMessageRequestDto(
                    scenarioId = scenarioId,
                    scenarioSystemPrompt = scenario.systemPrompt,
                    history = history.map { MessageDto(it.role.name.lowercase(), it.content) },
                    userMessage = userMessage,
                )
            )

            ConversationMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                content = response.content,
                correction = response.correction?.let { c ->
                    GrammarCorrection(
                        original = c.original,
                        corrected = c.corrected,
                        explanation = c.explanation,
                        correctionType = runCatching { CorrectionType.valueOf(c.type.uppercase()) }
                            .getOrDefault(CorrectionType.GRAMMAR),
                    )
                },
                timestampMillis = System.currentTimeMillis(),
            )
        } catch (e: Exception) {
            localFallback(userMessage)
        }
    }

    private fun localFallback(userMessage: String) = ConversationMessage(
        id = UUID.randomUUID().toString(),
        role = MessageRole.ASSISTANT,
        content = "I'm sorry, I couldn't connect right now. Please check your connection and try again.",
        correction = null,
        timestampMillis = System.currentTimeMillis(),
    )
}
