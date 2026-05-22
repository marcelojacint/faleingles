package br.com.faleingles.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrase_reviews")
data class PhraseReviewEntity(
    @PrimaryKey val phraseId: String,
    val easeFactor: Float = 2.5f,
    val interval: Int = 1,
    val repetitions: Int = 0,
    val nextReviewEpochDay: Long = 0,
    val lastReviewedAt: Long = 0,
)
