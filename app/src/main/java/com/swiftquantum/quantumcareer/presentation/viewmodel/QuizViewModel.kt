package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.CategoryStats
import com.swiftquantum.quantumcareer.domain.repository.QuizRepository
import com.swiftquantum.quantumcareer.domain.repository.SubmitAnswerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Test start screen state
    val categories: List<CategoryStats> = emptyList(),
    val testHistory: TestHistory? = null,
    val hasActiveSession: Boolean = false,

    // Active test state
    val session: TestSession? = null,
    val currentQuestion: Question? = null,
    val selectedOption: Int? = null,
    val timeRemainingSeconds: Int = 0,
    val isSubmitting: Boolean = false,
    val lastAnswerResult: SubmitAnswerResult? = null,
    val showExplanation: Boolean = false,

    // Test result state
    val testResult: TestResult? = null,

    // Practice mode state
    val practiceQuestions: PracticeQuestions? = null,
    val isPracticeMode: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var questionStartTime: Long = 0

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load categories
            quizRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                }

            // Load test history
            quizRepository.getTestHistory()
                .onSuccess { history ->
                    _uiState.value = _uiState.value.copy(testHistory = history)
                }

            // Check for active session
            quizRepository.getCurrentSession()
                .onSuccess { session ->
                    _uiState.value = _uiState.value.copy(
                        hasActiveSession = session != null,
                        session = session,
                        currentQuestion = session?.currentQuestion,
                        timeRemainingSeconds = session?.timeRemainingSeconds ?: 0
                    )
                    if (session != null) {
                        startTimer()
                    }
                }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun startTest(config: TestConfig = TestConfig.DEFAULT) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            quizRepository.startTest(config)
                .onSuccess { session ->
                    questionStartTime = System.currentTimeMillis()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        session = session,
                        currentQuestion = session.currentQuestion,
                        timeRemainingSeconds = config.timeLimitMinutes * 60,
                        selectedOption = null,
                        showExplanation = false,
                        isPracticeMode = false
                    )
                    startTimer()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to start test"
                    )
                }
        }
    }

    fun resumeTest() {
        val session = _uiState.value.session ?: return
        questionStartTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(
            currentQuestion = session.currentQuestion,
            selectedOption = null,
            showExplanation = false
        )
        startTimer()
    }

    fun selectOption(optionIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedOption = optionIndex)
    }

    fun submitAnswer() {
        val session = _uiState.value.session ?: return
        val question = _uiState.value.currentQuestion ?: return
        val selectedOption = _uiState.value.selectedOption ?: return

        val timeSpent = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)

            quizRepository.submitAnswer(
                sessionId = session.id,
                questionId = question.id,
                selectedOption = selectedOption,
                timeSpentSeconds = timeSpent
            )
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        lastAnswerResult = result,
                        showExplanation = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = error.message ?: "Failed to submit answer"
                    )
                }
        }
    }

    fun nextQuestion() {
        val session = _uiState.value.session ?: return
        val result = _uiState.value.lastAnswerResult

        if (result?.isLastQuestion == true) {
            finishTest()
            return
        }

        val nextIndex = result?.nextQuestionIndex ?: (session.currentIndex + 1)
        val updatedSession = session.copy(currentIndex = nextIndex)

        questionStartTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(
            session = updatedSession,
            currentQuestion = updatedSession.currentQuestion,
            selectedOption = null,
            showExplanation = false,
            lastAnswerResult = null
        )
    }

    fun finishTest() {
        val session = _uiState.value.session ?: return
        timerJob?.cancel()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            quizRepository.finishTest(session.id)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        testResult = result,
                        session = null,
                        currentQuestion = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to finish test"
                    )
                }
        }
    }

    fun abandonTest() {
        val session = _uiState.value.session ?: return
        timerJob?.cancel()

        viewModelScope.launch {
            quizRepository.abandonSession(session.id)
            _uiState.value = _uiState.value.copy(
                session = null,
                currentQuestion = null,
                hasActiveSession = false,
                timeRemainingSeconds = 0
            )
        }
    }

    fun loadPracticeQuestions(
        category: QuestionCategory? = null,
        difficulty: QuestionDifficulty? = null,
        count: Int = 10
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            quizRepository.getPracticeQuestions(category, difficulty, count)
                .onSuccess { practice ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        practiceQuestions = practice,
                        isPracticeMode = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load practice questions"
                    )
                }
        }
    }

    fun loadTestResult(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            quizRepository.getTestResult(sessionId)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        testResult = result
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load test result"
                    )
                }
        }
    }

    fun clearTestResult() {
        _uiState.value = _uiState.value.copy(testResult = null)
        loadInitialData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        loadInitialData()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.timeRemainingSeconds > 0) {
                delay(1000)
                val newTime = _uiState.value.timeRemainingSeconds - 1
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = newTime)

                if (newTime <= 0) {
                    finishTest()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
