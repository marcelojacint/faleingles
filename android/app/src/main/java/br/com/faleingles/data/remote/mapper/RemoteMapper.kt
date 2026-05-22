package br.com.faleingles.data.remote.mapper

import br.com.faleingles.data.remote.dto.LessonDetailDto
import br.com.faleingles.data.remote.dto.PhraseDto
import br.com.faleingles.data.remote.dto.WordDto
import br.com.faleingles.domain.model.GrammaticalType
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.domain.model.Word

fun LessonDetailDto.toDomain(isUnlocked: Boolean): Lesson = Lesson(
    id = id,
    phase = phase,
    orderInPhase = orderInPhase,
    title = title,
    situationContext = situationContext,
    isPremium = isPremium,
    isUnlocked = isUnlocked,
    phrases = phrases.map { it.toDomain() },
)

fun PhraseDto.toDomain(): Phrase = Phrase(
    id = id,
    affirmative = affirmative,
    negative = negative,
    interrogative = interrogative,
    audioUrl = audioUrl,
    words = words.map { it.toDomain() },
)

fun WordDto.toDomain(): Word = Word(
    text = text,
    translation = translation,
    grammaticalType = runCatching { GrammaticalType.valueOf(grammaticalType.uppercase()) }
        .getOrDefault(GrammaticalType.VERB),
    roleInPhrase = roleInPhrase,
)
