package br.com.faleingles.data.remote.api

import br.com.faleingles.data.remote.dto.*
import retrofit2.http.*

interface FaleInglesApi {

    @GET("api/lessons")
    suspend fun getLessons(): List<LessonSummaryDto>

    @GET("api/lessons/{id}")
    suspend fun getLessonById(@Path("id") id: String): LessonDetailDto

    @POST("api/conversation/message")
    suspend fun sendConversationMessage(@Body request: SendMessageRequestDto): AiResponseDto

    @GET("api/progress")
    suspend fun getUserProgress(): ProgressDto

    @POST("api/progress/lessons/{lessonId}/complete")
    suspend fun completeLesson(@Path("lessonId") lessonId: String)
}

data class ProgressDto(
    @com.google.gson.annotations.SerializedName("userId") val userId: String,
    @com.google.gson.annotations.SerializedName("currentStreakDays") val currentStreakDays: Int,
    @com.google.gson.annotations.SerializedName("totalMinutesStudied") val totalMinutesStudied: Int,
    @com.google.gson.annotations.SerializedName("todayMinutesStudied") val todayMinutesStudied: Int,
    @com.google.gson.annotations.SerializedName("dailyGoalMinutes") val dailyGoalMinutes: Int,
    @com.google.gson.annotations.SerializedName("isPro") val isPro: Boolean,
    @com.google.gson.annotations.SerializedName("completedLessonsCount") val completedLessonsCount: Int,
)
