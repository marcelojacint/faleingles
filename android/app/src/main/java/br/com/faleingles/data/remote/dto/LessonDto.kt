package br.com.faleingles.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LessonSummaryDto(
    @SerializedName("id") val id: String,
    @SerializedName("phase") val phase: Int,
    @SerializedName("orderInPhase") val orderInPhase: Int,
    @SerializedName("title") val title: String,
    @SerializedName("situationContext") val situationContext: String,
    @SerializedName("isPremium") val isPremium: Boolean,
    @SerializedName("isUnlocked") val isUnlocked: Boolean,
    @SerializedName("phraseCount") val phraseCount: Int,
)

data class LessonDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("phase") val phase: Int,
    @SerializedName("orderInPhase") val orderInPhase: Int,
    @SerializedName("title") val title: String,
    @SerializedName("situationContext") val situationContext: String,
    @SerializedName("isPremium") val isPremium: Boolean,
    @SerializedName("phrases") val phrases: List<PhraseDto>,
)

data class PhraseDto(
    @SerializedName("id") val id: String,
    @SerializedName("affirmative") val affirmative: String,
    @SerializedName("negative") val negative: String,
    @SerializedName("interrogative") val interrogative: String,
    @SerializedName("audioUrl") val audioUrl: String,
    @SerializedName("words") val words: List<WordDto>,
)

data class WordDto(
    @SerializedName("text") val text: String,
    @SerializedName("translation") val translation: String,
    @SerializedName("grammaticalType") val grammaticalType: String,
    @SerializedName("roleInPhrase") val roleInPhrase: String,
)
