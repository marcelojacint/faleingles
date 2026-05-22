package br.com.faleingles.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.faleingles.data.local.dao.LessonDao
import br.com.faleingles.data.local.dao.PhraseReviewDao
import br.com.faleingles.data.local.dao.UserProgressDao
import br.com.faleingles.data.local.entity.*

@Database(
    entities = [
        LessonEntity::class,
        PhraseEntity::class,
        WordEntity::class,
        UserProgressEntity::class,
        PhraseReviewEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun phraseReviewDao(): PhraseReviewDao
}
