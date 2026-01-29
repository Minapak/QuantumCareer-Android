package com.swiftquantum.quantumcareer.domain.model

/**
 * Australian Quantum Standards (SQC) Fidelity Grading System
 * Based on Silicon Quantum Computing (SQC) standards v5.2.0
 *
 * Fidelity grades represent the accuracy and reliability of quantum operations,
 * following Australian national quantum computing standards.
 */

/**
 * SQC Fidelity Grade representing quantum operation accuracy levels
 * aligned with Australian Standards v5.2.0
 */
enum class FidelityGrade(
    val minFidelity: Double,
    val maxFidelity: Double,
    val displayName: String,
    val description: String
) {
    PLATINUM(
        minFidelity = 99.9,
        maxFidelity = 100.0,
        displayName = "Platinum",
        description = "World-class quantum fidelity exceeding 99.9%"
    ),
    GOLD(
        minFidelity = 99.5,
        maxFidelity = 99.9,
        displayName = "Gold",
        description = "Excellent quantum fidelity between 99.5% and 99.9%"
    ),
    SILVER(
        minFidelity = 99.0,
        maxFidelity = 99.5,
        displayName = "Silver",
        description = "High quantum fidelity between 99.0% and 99.5%"
    ),
    BRONZE(
        minFidelity = 98.0,
        maxFidelity = 99.0,
        displayName = "Bronze",
        description = "Good quantum fidelity between 98.0% and 99.0%"
    ),
    STANDARD(
        minFidelity = 95.0,
        maxFidelity = 98.0,
        displayName = "Standard",
        description = "Baseline quantum fidelity between 95.0% and 98.0%"
    ),
    DEVELOPING(
        minFidelity = 0.0,
        maxFidelity = 95.0,
        displayName = "Developing",
        description = "Quantum fidelity below 95.0%, room for improvement"
    );

    companion object {
        /**
         * Determines the fidelity grade based on a percentage value
         */
        fun fromPercentage(percentage: Double): FidelityGrade {
            return when {
                percentage >= 99.9 -> PLATINUM
                percentage >= 99.5 -> GOLD
                percentage >= 99.0 -> SILVER
                percentage >= 98.0 -> BRONZE
                percentage >= 95.0 -> STANDARD
                else -> DEVELOPING
            }
        }

        /**
         * Parses a fidelity grade from a string value
         */
        fun fromString(value: String): FidelityGrade {
            return when (value.lowercase()) {
                "platinum" -> PLATINUM
                "gold" -> GOLD
                "silver" -> SILVER
                "bronze" -> BRONZE
                "standard" -> STANDARD
                "developing" -> DEVELOPING
                else -> DEVELOPING
            }
        }
    }

    /**
     * Returns the next higher grade, or null if already at highest
     */
    val nextGrade: FidelityGrade?
        get() = when (this) {
            DEVELOPING -> STANDARD
            STANDARD -> BRONZE
            BRONZE -> SILVER
            SILVER -> GOLD
            GOLD -> PLATINUM
            PLATINUM -> null
        }

    /**
     * Returns true if this grade meets or exceeds the specified grade
     */
    fun meetsOrExceeds(grade: FidelityGrade): Boolean {
        return this.ordinal <= grade.ordinal
    }
}

/**
 * Australian Standards certification information
 */
data class AustralianStandardsCertification(
    val standardsVersion: String = "5.2.0",
    val fidelityGrade: FidelityGrade,
    val measuredFidelity: Double,
    val certificationDate: String,
    val expiryDate: String?,
    val certificationBody: String = "SQC Australia",
    val certificateId: String,
    val isActive: Boolean = true
) {
    /**
     * Returns the percentage to next grade
     */
    val progressToNextGrade: Float
        get() {
            val nextGrade = fidelityGrade.nextGrade ?: return 1.0f
            val currentMin = fidelityGrade.minFidelity
            val nextMin = nextGrade.minFidelity
            val range = nextMin - currentMin
            val progress = measuredFidelity - currentMin
            return (progress / range).toFloat().coerceIn(0f, 1f)
        }

    /**
     * Returns the points needed to reach next grade
     */
    val pointsToNextGrade: Double
        get() {
            val nextGrade = fidelityGrade.nextGrade ?: return 0.0
            return (nextGrade.minFidelity - measuredFidelity).coerceAtLeast(0.0)
        }
}

/**
 * SQC Fidelity metrics for quantum circuits
 */
data class SQCFidelityMetrics(
    val singleQubitGateFidelity: Double,
    val twoQubitGateFidelity: Double,
    val readoutFidelity: Double,
    val overallFidelity: Double,
    val coherenceTime: Double?, // in microseconds
    val gateTime: Double?, // in nanoseconds
    val measurementDate: String
) {
    val grade: FidelityGrade
        get() = FidelityGrade.fromPercentage(overallFidelity)

    val formattedOverallFidelity: String
        get() = String.format("%.2f%%", overallFidelity)

    val formattedSingleQubitFidelity: String
        get() = String.format("%.2f%%", singleQubitGateFidelity)

    val formattedTwoQubitFidelity: String
        get() = String.format("%.2f%%", twoQubitGateFidelity)

    val formattedReadoutFidelity: String
        get() = String.format("%.2f%%", readoutFidelity)
}
