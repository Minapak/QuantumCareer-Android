package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface QuizRepository {

    /**
     * Start a new test session.
     */
    suspend fun startTest(config: TestConfig = TestConfig.DEFAULT): Result<TestSession>

    /**
     * Submit an answer for the current question.
     */
    suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        selectedOption: Int,
        timeSpentSeconds: Int
    ): Result<SubmitAnswerResult>

    /**
     * Finish the test and get results.
     */
    suspend fun finishTest(sessionId: String): Result<TestResult>

    /**
     * Get test history.
     */
    suspend fun getTestHistory(page: Int = 1, perPage: Int = 10): Result<TestHistory>

    /**
     * Get practice questions.
     */
    suspend fun getPracticeQuestions(
        category: QuestionCategory? = null,
        difficulty: QuestionDifficulty? = null,
        count: Int = 10
    ): Result<PracticeQuestions>

    /**
     * Get current active test session.
     */
    suspend fun getCurrentSession(): Result<TestSession?>

    /**
     * Abandon current test session.
     */
    suspend fun abandonSession(sessionId: String): Result<Boolean>

    /**
     * Get a specific test result.
     */
    suspend fun getTestResult(sessionId: String): Result<TestResult>

    /**
     * Get available categories with stats.
     */
    suspend fun getCategories(): Result<List<CategoryStats>>
}

/**
 * Result of submitting an answer.
 */
data class SubmitAnswerResult(
    val isCorrect: Boolean,
    val correctAnswer: Int,
    val explanation: String,
    val nextQuestionIndex: Int?,
    val isLastQuestion: Boolean
)

/**
 * Category statistics for the user.
 */
data class CategoryStats(
    val category: QuestionCategory,
    val displayName: String,
    val description: String,
    val totalQuestions: Int,
    val userAccuracy: Float?
)
