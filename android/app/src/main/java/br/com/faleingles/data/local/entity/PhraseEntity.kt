package br.com.faleingles.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "phrases",
    foreignKeys = [ForeignKey(
        entity = LessonEntity::class,
        parentColumns = ["id"],
        childColumns = ["lessonId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("lessonId")],
)
data class PhraseEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val affirmative: String,
    val negative: String,
    val interrogative: String,
    val audioUrl: String,
    val orderInLesson: Int,
)
