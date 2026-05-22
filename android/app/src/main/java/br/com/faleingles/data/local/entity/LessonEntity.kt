package br.com.faleingles.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val phase: Int,
    val orderInPhase: Int,
    val title: String,
    val situationContext: String,
    val isPremium: Boolean,
    val updatedAt: Long = System.currentTimeMillis(),
)
