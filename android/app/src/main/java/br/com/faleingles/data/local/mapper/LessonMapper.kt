package br.com.faleingles.data.local.mapper

import br.com.faleingles.data.local.dao.LessonWithPhrasesAndWords
import br.com.faleingles.data.local.dao.PhraseWithWords
import br.com.faleingles.data.local.entity.LessonEntity
import br.com.faleingles.data.local.entity.PhraseEntity
import br.com.faleingles.data.local.entity.WordEntity
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.domain.model.Word

fun LessonWithPhrasesAndWords.toDomain(isPro: Boolean): Lesson = Lesson(
    id = lesson.id,
    phase = lesson.phase,
    orderInPhase = lesson.orderInPhase,
    title = lesson.title,
    situationContext = lesson.situationContext,
    isPremium = lesson.isPremium,
    isUnlocked = !lesson.isPremium || isPro || (lesson.phase == 1 && lesson.orderInPhase <= 3),
    phrases = phrases.sortedBy { it.phrase.orderInLesson }.map { it.toDomain() },
)

fun PhraseWithWords.toDomain(): Phrase = Phrase(
    id = phrase.id,
    affirmative = phrase.affirmative,
    negative = phrase.negative,
    interrogative = phrase.interrogative,
    audioUrl = phrase.audioUrl,
    words = words.sortedBy { it.orderInPhrase }.map { it.toDomain() },
)

fun WordEntity.toDomain(): Word = Word(
    text = text,
    translation = translation,
    grammaticalType = grammaticalType,
    roleInPhrase = roleInPhrase,
)

fun Lesson.toEntity(): LessonEntity = LessonEntity(
    id = id,
    phase = phase,
    orderInPhase = orderInPhase,
    title = title,
    situationContext = situationContext,
    isPremium = isPremium,
)

fun Phrase.toEntity(lessonId: String, order: Int): PhraseEntity = PhraseEntity(
    id = id,
    lessonId = lessonId,
    affirmative = affirmative,
    negative = negative,
    interrogative = interrogative,
    audioUrl = audioUrl,
    orderInLesson = order,
)

fun Word.toEntity(phraseId: String, order: Int): WordEntity = WordEntity(
    phraseId = phraseId,
    text = text,
    translation = translation,
    grammaticalType = grammaticalType,
    roleInPhrase = roleInPhrase,
    orderInPhrase = order,
)
