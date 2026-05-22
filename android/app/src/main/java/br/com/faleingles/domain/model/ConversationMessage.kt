package br.com.faleingles.domain.model

data class ConversationMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val correction: GrammarCorrection? = null,
    val timestampMillis: Long,
)

data class GrammarCorrection(
    val original: String,
    val corrected: String,
    val explanation: String,
    val correctionType: CorrectionType,
)

enum class MessageRole { USER, ASSISTANT }

enum class CorrectionType { GRAMMAR, PRONUNCIATION, NATURALNESS }

data class ConversationScenario(
    val id: String,
    val title: String,
    val description: String,
    val systemPrompt: String,
    val requiredPhase: Int,
)
