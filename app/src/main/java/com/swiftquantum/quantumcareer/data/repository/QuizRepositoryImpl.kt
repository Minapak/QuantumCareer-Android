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
                } ?: Result.failure(Exception("Failed to get test history"))
            } else {
                Result.failure(Exception("Failed to get test history: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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
                } ?: Result.failure(Exception("Failed to get practice questions"))
            } else {
                Result.failure(Exception("Failed to get practice questions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentSession(): Result<TestSession?> {
        return try {
            val response = api.getCurrentSession()
            if (response.isSuccessful) {
                Result.success(response.body()?.toDomain())
            } else if (response.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(Exception("Failed to get current session: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun abandonSession(sessionId: String): Result<Boolean> {
        return try {
            val response = api.abandonSession(sessionId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
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
                } ?: Result.failure(Exception("Failed to get categories"))
            } else {
                Result.failure(Exception("Failed to get categories: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
