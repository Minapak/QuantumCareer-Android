package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== Request DTOs ==============

@Serializable
data class StartTestRequestDto(
    @SerialName("total_questions") val totalQuestions: Int = 50,
    @SerialName("time_limit_minutes") val timeLimitMinutes: Int = 90,
    @SerialName("shuffle_questions") val shuffleQuestions: Boolean = true,
    @SerialName("shuffle_options") val shuffleOptions: Boolean = true,
    @SerialName("categories") val categories: List<String>? = null
)

@Serializable
data class SubmitAnswerRequestDto(
    @SerialName("session_id") val sessionId: String,
    @SerialName("question_id") val questionId: String,
    @SerialName("selected_option") val selectedOption: Int,
    @SerialName("time_spent_seconds") val timeSpentSeconds: Int
)

@Serializable
data class FinishTestRequestDto(
    @SerialName("session_id") val sessionId: String
)

// ============== Response DTOs ==============

@Serializable
data class QuestionDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("options") val options: List<String>,
    @SerialName("correct_answer") val correctAnswer: Int,
    @SerialName("difficulty") val difficulty: String,
    @SerialName("category") val category: String,
    @SerialName("explanation") val explanation: String
)

@Serializable
data class QuizAnswerDto(
    @SerialName("question_id") val questionId: String,
    @SerialName("selected_option") val selectedOption: Int,
    @SerialName("is_correct") val isCorrect: Boolean,
    @SerialName("time_spent_seconds") val timeSpentSeconds: Int
)

@Serializable
data class TestSessionDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("questions") val questions: List<QuestionDto>,
    @SerialName("current_index") val currentIndex: Int,
    @SerialName("answers") val answers: List<QuizAnswerDto>,
    @SerialName("start_time") val startTime: String,
    @SerialName("status") val status: String,
    @SerialName("time_remaining_seconds") val timeRemainingSeconds: Int? = null
)

@Serializable
data class SubmitAnswerResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("is_correct") val isCorrect: Boolean,
    @SerialName("correct_answer") val correctAnswer: Int,
    @SerialName("explanation") val explanation: String,
    @SerialName("next_question_index") val nextQuestionIndex: Int?,
    @SerialName("is_last_question") val isLastQuestion: Boolean
)

@Serializable
data class CategoryBreakdownDto(
    @SerialName("category") val category: String,
    @SerialName("total_questions") val totalQuestions: Int,
    @SerialName("correct_answers") val correctAnswers: Int,
    @SerialName("total_points") val totalPoints: Int,
    @SerialName("earned_points") val earnedPoints: Int,
    @SerialName("average_time_seconds") val averageTimeSeconds: Float
)

@Serializable
data class TestResultDto(
    @SerialName("session_id") val sessionId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("score") val score: Int,
    @SerialName("max_score") val maxScore: Int,
    @SerialName("passing_score") val passingScore: Int,
    @SerialName("passed") val passed: Boolean,
    @SerialName("badge_earned") val badgeEarned: String?,
    @SerialName("category_breakdown") val categoryBreakdown: List<CategoryBreakdownDto>,
    @SerialName("total_time_spent_seconds") val totalTimeSpentSeconds: Int,
    @SerialName("completed_at") val completedAt: String,
    @SerialName("certificate_id") val certificateId: String?
)

@Serializable
data class TestHistoryItemDto(
    @SerialName("session_id") val sessionId: String,
    @SerialName("score") val score: Int,
    @SerialName("max_score") val maxScore: Int,
    @SerialName("passed") val passed: Boolean,
    @SerialName("badge_earned") val badgeEarned: String?,
    @SerialName("completed_at") val completedAt: String,
    @SerialName("time_spent_seconds") val timeSpentSeconds: Int
)

@Serializable
data class TestHistoryResponseDto(
    @SerialName("results") val results: List<TestResultDto>,
    @SerialName("total_attempts") val totalAttempts: Int,
    @SerialName("best_score") val bestScore: Int,
    @SerialName("best_badge") val bestBadge: String?,
    @SerialName("average_score") val averageScore: Float,
    @SerialName("total_time_spent_seconds") val totalTimeSpentSeconds: Int,
    @SerialName("page") val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class PracticeQuestionsResponseDto(
    @SerialName("category") val category: String?,
    @SerialName("difficulty") val difficulty: String?,
    @SerialName("questions") val questions: List<QuestionDto>,
    @SerialName("total") val total: Int
)

@Serializable
data class CategoryStatsDto(
    @SerialName("category") val category: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("description") val description: String,
    @SerialName("total_questions") val totalQuestions: Int,
    @SerialName("user_accuracy") val userAccuracy: Float?
)

@Serializable
data class CategoriesResponseDto(
    @SerialName("categories") val categories: List<CategoryStatsDto>
)
