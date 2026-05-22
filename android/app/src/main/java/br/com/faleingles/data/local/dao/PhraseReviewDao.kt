package br.com.faleingles.data.local.dao

import androidx.room.*
import br.com.faleingles.data.local.entity.PhraseReviewEntity

@Dao
interface PhraseReviewDao {
    @Query("SELECT * FROM phrase_reviews WHERE nextReviewEpochDay <= :todayEpochDay ORDER BY nextReviewEpochDay LIMIT 20")
    suspend fun getDueForReview(todayEpochDay: Long): List<PhraseReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PhraseReviewEntity)

    @Query("SELECT COUNT(*) FROM phrase_reviews WHERE nextReviewEpochDay <= :todayEpochDay")
    suspend fun countDueForReview(todayEpochDay: Long): Int
}
