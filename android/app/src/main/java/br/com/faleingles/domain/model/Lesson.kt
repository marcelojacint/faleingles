package br.com.faleingles.domain.model

data class Lesson(
    val id: String,
    val phase: Int,
    val orderInPhase: Int,
    val title: String,
    val situationContext: String,
    val phrases: List<Phrase>,
    val isUnlocked: Boolean,
    val isPremium: Boolean,
)

data class Phrase(
    val id: String,
    val affirmative: String,
    val negative: String,
    val interrogative: String,
    val audioUrl: String,
    val words: List<Word>,
)

data class Word(
    val text: String,
    val translation: String,
    val grammaticalType: GrammaticalType,
    val roleInPhrase: String,
)

enum class GrammaticalType {
    SUBJECT,
    VERB,
    OBJECT,
    ADJECTIVE,
    ADVERB,
    PREPOSITION,
    ARTICLE,
    CONJUNCTION,
    PRONOUN,
    AUXILIARY,
}
