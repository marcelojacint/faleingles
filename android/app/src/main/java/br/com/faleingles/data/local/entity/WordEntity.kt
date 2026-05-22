package br.com.faleingles.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.faleingles.domain.model.GrammaticalType

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = PhraseEntity::class,
        parentColumns = ["id"],
        childColumns = ["phraseId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("phraseId")],
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phraseId: String,
    val text: String,
    val translation: String,
    val grammaticalType: GrammaticalType,
    val roleInPhrase: String,
    val orderInPhrase: Int,
)
