package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface QuizApi {

    /**
     * Start a new 50-question certification test.
     */
    @POST("career-passport/quiz/start")
    suspend fun startTest(
        @Body request: StartTestRequestDto
    ): Response<TestSessionDto>

    /**
     * Submit an answer for the current question.
     */
    @POST("career-passport/quiz/answer")
    suspend fun submitAnswer(
        @Body request: SubmitAnswerRequestDto
    ): Response<SubmitAnswerResponseDto>

    /**
     * Complete the test and get final results.
     */
    @POST("career-passport/quiz/finish")
    suspend fun finishTest(
        @Body request: FinishTestRequestDto
    ): Response<TestResultDto>

    /**
     * Get the user's test history.
     */
    @GET("career-passport/quiz/history")
    suspend fun getTestHistory(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): Response<TestHistoryResponseDto>

    /**
     * Get practice questions (optional category and difficulty filters).
     */
    @GET("career-passport/quiz/questions/practice")
    suspend fun getPracticeQuestions(
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("count") count: Int = 10
    ): Response<PracticeQuestionsResponseDto>

    /**
     * Get current test session (if any).
     */
    @GET("career-passport/quiz/session")
    suspend fun getCurrentSession(): Response<TestSessionDto?>

    /**
     * Abandon current test session.
     */
    @DELETE("career-passport/quiz/session/{sessionId}")
    suspend fun abandonSession(
        @Path("sessionId") sessionId: String
    ): Response<Unit>

    /**
     * Get a specific test result.
     */
    @GET("career-passport/quiz/results/{sessionId}")
    suspend fun getTestResult(
        @Path("sessionId") sessionId: String
    ): Response<TestResultDto>

    /**
     * Get available question categories with statistics.
     */
    @GET("career-passport/quiz/categories")
    suspend fun getCategories(): Response<CategoriesResponseDto>
}
