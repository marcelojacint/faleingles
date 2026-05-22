package br.com.faleingles.core.di

import android.content.Context
import androidx.room.Room
import br.com.faleingles.data.local.AppDatabase
import br.com.faleingles.data.local.dao.LessonDao
import br.com.faleingles.data.local.dao.PhraseReviewDao
import br.com.faleingles.data.local.dao.UserProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "faleingles.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()
    @Provides fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()
    @Provides fun providePhraseReviewDao(db: AppDatabase): PhraseReviewDao = db.phraseReviewDao()
}
