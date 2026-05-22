package br.com.faleingles.data.local.dao

import androidx.room.*
import br.com.faleingles.data.local.entity.LessonEntity
import br.com.faleingles.data.local.entity.PhraseEntity
import br.com.faleingles.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

data class LessonWithPhrasesAndWords(
    @Embedded val lesson: LessonEntity,
    @Relation(
        entity = PhraseEntity::class,
        parentColumn = "id",
        entityColumn = "lessonId",
    )
    val phrases: List<PhraseWithWords>,
)

data class PhraseWithWords(
    @Embedded val phrase: PhraseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "phraseId",
    )
    val words: List<WordEntity>,
)

@Dao
interface LessonDao {
    @Transaction
    @Query("SELECT * FROM lessons ORDER BY phase, orderInPhase")
    fun observeAll(): Flow<List<LessonWithPhrasesAndWords>>

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getById(id: String): LessonWithPhrasesAndWords?

    @Transaction
    @Query("SELECT * FROM lessons WHERE phase = :phase ORDER BY orderInPhase")
    suspend fun getByPhase(phase: Int): List<LessonWithPhrasesAndWords>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessons(lessons: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPhrases(phrases: List<PhraseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWords(words: List<WordEntity>)

    @Query("DELETE FROM lessons")
    suspend fun clearAll()
}
