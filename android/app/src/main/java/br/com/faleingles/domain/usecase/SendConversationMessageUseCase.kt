package br.com.faleingles.domain.usecase

import br.com.faleingles.domain.model.ConversationMessage
import br.com.faleingles.domain.repository.ConversationRepository
import javax.inject.Inject

class SendConversationMessageUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
) {
    suspend operator fun invoke(
        scenarioId: String,
        history: List<ConversationMessage>,
        userMessage: String,
    ): Result<ConversationMessage> = runCatching {
        conversationRepository.sendMessage(scenarioId, history, userMessage)
    }
}
