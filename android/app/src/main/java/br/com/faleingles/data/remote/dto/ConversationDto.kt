package br.com.faleingles.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SendMessageRequestDto(
    @SerializedName("scenarioId") val scenarioId: String,
    @SerializedName("scenarioSystemPrompt") val scenarioSystemPrompt: String,
    @SerializedName("history") val history: List<MessageDto>,
    @SerializedName("userMessage") val userMessage: String,
)

data class MessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
)

data class AiResponseDto(
    @SerializedName("content") val content: String,
    @SerializedName("correction") val correction: CorrectionDto?,
)

data class CorrectionDto(
    @SerializedName("original") val original: String,
    @SerializedName("corrected") val corrected: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("type") val type: String,
)
