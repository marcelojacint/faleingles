package br.com.faleingles.core.di

import br.com.faleingles.data.repository.ConversationRepositoryImpl
import br.com.faleingles.data.repository.LessonRepositoryImpl
import br.com.faleingles.data.repository.ProgressRepositoryImpl
import br.com.faleingles.domain.repository.ConversationRepository
import br.com.faleingles.domain.repository.LessonRepository
import br.com.faleingles.domain.repository.ProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLessonRepository(impl: LessonRepositoryImpl): LessonRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository
}
