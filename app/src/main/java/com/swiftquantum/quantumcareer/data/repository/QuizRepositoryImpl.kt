package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.QuizApi
import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.CategoryStats
import com.swiftquantum.quantumcareer.domain.repository.QuizRepository
import com.swiftquantum.quantumcareer.domain.repository.SubmitAnswerResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val api: QuizApi
) : QuizRepository {

    override suspend fun startTest(config: TestConfig): Result<TestSession> {
        return try {
            val request = StartTestRequestDto(
                totalQuestions = config.totalQuestions,
                timeLimitMinutes = config.timeLimitMinutes,
                shuffleQuestions = config.shuffleQuestions,
                shuffleOptions = config.shuffleOptions,
                categories = config.categories?.map { it.name.lowercase() }
            )
            val response = api.startTest(request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to start test"))
            } else {
                Result.failure(Exception("Failed to start test: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        selectedOption: Int,
        timeSpentSeconds: Int
    ): Result<SubmitAnswerResult> {
        return try {
            val request = SubmitAnswerRequestDto(
                sessionId = sessionId,
                questionId = questionId,
                selectedOption = selectedOption,
                timeSpentSeconds = timeSpentSeconds
            )
            val response = api.submitAnswer(request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(
                        SubmitAnswerResult(
                            isCorrect = dto.isCorrect,
                            correctAnswer = dto.correctAnswer,
                            explanation = dto.explanation,
                            nextQuestionIndex = dto.nextQuestionIndex,
                            isLastQuestion = dto.isLastQuestion
                        )
                    )
                } ?: Result.failure(Exception("Failed to submit answer"))
            } else {
                Result.failure(Exception("Failed to submit answer: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun finishTest(sessionId: String): Result<TestResult> {
        return try {
            val request = FinishTestRequestDto(sessionId = sessionId)
            val response = api.finishTest(request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to finish test"))
            } else {
                Result.failure(Exception("Failed to finish test: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTestHistory(page: Int, perPage: Int): Result<TestHistory> {
        return try {
            val response = api.getTestHistory(page, perPage)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.success(getDefaultTestHistory())
            } else {
                // Return default test history for guest/offline mode
                Result.success(getDefaultTestHistory())
            }
        } catch (e: Exception) {
            // Return default test history on API failure
            Result.success(getDefaultTestHistory())
        }
    }

    private fun getDefaultTestHistory(): TestHistory {
        return TestHistory(
            results = emptyList(),
            totalAttempts = 0,
            bestScore = 0,
            bestBadge = null,
            averageScore = 0f,
            totalTimeSpentSeconds = 0
        )
    }

    override suspend fun getPracticeQuestions(
        category: QuestionCategory?,
        difficulty: QuestionDifficulty?,
        count: Int
    ): Result<PracticeQuestions> {
        return try {
            val response = api.getPracticeQuestions(
                category = category?.name?.lowercase(),
                difficulty = difficulty?.name?.lowercase(),
                count = count
            )
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.success(getEmptyPracticeQuestions(category, difficulty))
            } else {
                // Return empty practice questions for guest/offline mode
                Result.success(getEmptyPracticeQuestions(category, difficulty))
            }
        } catch (e: Exception) {
            // Return empty practice questions on API failure
            Result.success(getEmptyPracticeQuestions(category, difficulty))
        }
    }

    private fun getEmptyPracticeQuestions(
        category: QuestionCategory?,
        difficulty: QuestionDifficulty?
    ): PracticeQuestions {
        return PracticeQuestions(
            category = category,
            difficulty = difficulty,
            questions = emptyList(),
            total = 0
        )
    }

    override suspend fun getCurrentSession(): Result<TestSession?> {
        return try {
            val response = api.getCurrentSession()
            if (response.isSuccessful) {
                Result.success(response.body()?.toDomain())
            } else if (response.code() == 404) {
                Result.success(null)
            } else {
                // Return null for guest/offline mode (no active session)
                Result.success(null)
            }
        } catch (e: Exception) {
            // Return null on API failure (no active session)
            Result.success(null)
        }
    }

    override suspend fun abandonSession(sessionId: String): Result<Boolean> {
        return try {
            val response = api.abandonSession(sessionId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            // Return false on API failure - non-critical operation
            Result.success(false)
        }
    }

    override suspend fun getTestResult(sessionId: String): Result<TestResult> {
        return try {
            val response = api.getTestResult(sessionId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get test result"))
            } else {
                Result.failure(Exception("Failed to get test result: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): Result<List<CategoryStats>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.categories.map { it.toDomain() })
                } ?: Result.success(getDefaultCategories())
            } else {
                // Return default categories for guest/offline mode
                Result.success(getDefaultCategories())
            }
        } catch (e: Exception) {
            // Return default categories on API failure
            Result.success(getDefaultCategories())
        }
    }

    private fun getDefaultCategories(): List<CategoryStats> {
        return QuestionCategory.values().map { category ->
            CategoryStats(
                category = category,
                displayName = category.displayName,
                description = category.description,
                totalQuestions = 0,
                userAccuracy = null
            )
        }
    }
}
