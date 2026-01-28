package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

/**
 * Represents a question category in the quantum computing certification test.
 */
enum class QuestionCategory {
    BASICS,
    GATES,
    CIRCUITS,
    ALGORITHMS,
    HARDWARE,
    APPLICATIONS;

    companion object {
        fun fromString(value: String): QuestionCategory {
            return when (value.uppercase()) {
                "BASICS" -> BASICS
                "GATES" -> GATES
                "CIRCUITS" -> CIRCUITS
                "ALGORITHMS" -> ALGORITHMS
                "HARDWARE" -> HARDWARE
                "APPLICATIONS" -> APPLICATIONS
                else -> BASICS
            }
        }
    }

    val displayName: String
        get() = when (this) {
            BASICS -> "Quantum Basics"
            GATES -> "Quantum Gates"
            CIRCUITS -> "Circuit Design"
            ALGORITHMS -> "Algorithms"
            HARDWARE -> "Hardware"
            APPLICATIONS -> "Applications"
        }

    val description: String
        get() = when (this) {
            BASICS -> "Fundamental concepts of quantum computing"
            GATES -> "Single and multi-qubit gate operations"
            CIRCUITS -> "Quantum circuit design and optimization"
            ALGORITHMS -> "Quantum algorithms and complexity"
            HARDWARE -> "Physical implementations and error correction"
            APPLICATIONS -> "Real-world applications and use cases"
        }
}

/**
 * Represents the difficulty level of a question.
 */
enum class QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT;

    companion object {
        fun fromString(value: String): QuestionDifficulty {
            return when (value.uppercase()) {
                "EASY" -> EASY
                "MEDIUM" -> MEDIUM
                "HARD" -> HARD
                "EXPERT" -> EXPERT
                else -> EASY
            }
        }
    }

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val points: Int
        get() = when (this) {
            EASY -> 1
            MEDIUM -> 2
            HARD -> 3
            EXPERT -> 5
        }

    val color: Long
        get() = when (this) {
            EASY -> 0xFF4CAF50  // Green
            MEDIUM -> 0xFFFF9800  // Orange
            HARD -> 0xFFF44336  // Red
            EXPERT -> 0xFF9C27B0  // Purple
        }
}

/**
 * Represents a single quiz question with 4 options.
 */
data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val difficulty: QuestionDifficulty,
    val category: QuestionCategory,
    val explanation: String
) {
    init {
        require(options.size == 4) { "Question must have exactly 4 options" }
        require(correctAnswer in 0..3) { "Correct answer must be between 0 and 3" }
    }

    val points: Int
        get() = difficulty.points
}

/**
 * Represents the status of a test session.
 */
enum class TestStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED,
    EXPIRED;

    companion object {
        fun fromString(value: String): TestStatus {
            return when (value.uppercase()) {
                "IN_PROGRESS" -> IN_PROGRESS
                "COMPLETED" -> COMPLETED
                "ABANDONED" -> ABANDONED
                "EXPIRED" -> EXPIRED
                else -> IN_PROGRESS
            }
        }
    }

    val displayName: String
        get() = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * Represents an answer submitted during a test session.
 */
data class QuizAnswer(
    val questionId: String,
    val selectedOption: Int,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int
)

/**
 * Represents an active test session with 50 questions.
 */
data class TestSession(
    val id: String,
    val userId: String,
    val questions: List<Question>,
    val currentIndex: Int,
    val answers: List<QuizAnswer>,
    val startTime: LocalDateTime,
    val status: TestStatus,
    val timeRemainingSeconds: Int? = null
) {
    val totalQuestions: Int
        get() = questions.size

    val answeredCount: Int
        get() = answers.size

    val progress: Float
        get() = if (totalQuestions > 0) answeredCount.toFloat() / totalQuestions else 0f

    val currentQuestion: Question?
        get() = questions.getOrNull(currentIndex)

    val isComplete: Boolean
        get() = answeredCount >= totalQuestions || status == TestStatus.COMPLETED

    val maxPossibleScore: Int
        get() = questions.sumOf { it.points }

    val currentScore: Int
        get() = answers.filter { it.isCorrect }.sumOf { answer ->
            questions.find { it.id == answer.questionId }?.points ?: 0
        }
}

/**
 * Represents the breakdown of performance by category.
 */
data class CategoryBreakdown(
    val category: QuestionCategory,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val totalPoints: Int,
    val earnedPoints: Int,
    val averageTimeSeconds: Float
) {
    val accuracy: Float
        get() = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f

    val scorePercentage: Float
        get() = if (totalPoints > 0) earnedPoints.toFloat() / totalPoints else 0f
}

/**
 * Represents the result of a completed test session.
 */
data class TestResult(
    val sessionId: String,
    val userId: String,
    val score: Int,
    val maxScore: Int,
    val passingScore: Int,
    val passed: Boolean,
    val badgeEarned: BadgeTier?,
    val categoryBreakdown: List<CategoryBreakdown>,
    val totalTimeSpentSeconds: Int,
    val completedAt: LocalDateTime,
    val certificateId: String?
) {
    val scorePercentage: Float
        get() = if (maxScore > 0) score.toFloat() / maxScore * 100 else 0f

    val passedPercentage: Float
        get() = if (maxScore > 0) passingScore.toFloat() / maxScore * 100 else 0f

    val formattedTimeSpent: String
        get() {
            val minutes = totalTimeSpentSeconds / 60
            val seconds = totalTimeSpentSeconds % 60
            return "${minutes}m ${seconds}s"
        }

    companion object {
        // Passing thresholds for badge tiers
        fun getBadgeTierForPercentage(percentage: Float): BadgeTier? {
            return when {
                percentage >= 95 -> BadgeTier.PLATINUM
                percentage >= 85 -> BadgeTier.GOLD
                percentage >= 75 -> BadgeTier.SILVER
                percentage >= 60 -> BadgeTier.BRONZE
                else -> null
            }
        }
    }
}

/**
 * Represents a summary of past test attempts.
 */
data class TestHistory(
    val results: List<TestResult>,
    val totalAttempts: Int,
    val bestScore: Int,
    val bestBadge: BadgeTier?,
    val averageScore: Float,
    val totalTimeSpentSeconds: Int
) {
    val passRate: Float
        get() = if (totalAttempts > 0) {
            results.count { it.passed }.toFloat() / totalAttempts
        } else 0f
}

/**
 * Configuration for starting a new test.
 */
data class TestConfig(
    val totalQuestions: Int = 50,
    val timeLimitMinutes: Int = 90,
    val shuffleQuestions: Boolean = true,
    val shuffleOptions: Boolean = true,
    val categories: List<QuestionCategory>? = null  // null means all categories
) {
    companion object {
        val DEFAULT = TestConfig()
        val PRACTICE = TestConfig(
            totalQuestions = 10,
            timeLimitMinutes = 15,
            shuffleQuestions = true,
            shuffleOptions = true
        )
    }
}

/**
 * Practice mode question set for a specific category.
 */
data class PracticeQuestions(
    val category: QuestionCategory?,
    val difficulty: QuestionDifficulty?,
    val questions: List<Question>,
    val total: Int
)
