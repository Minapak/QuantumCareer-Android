package com.swiftquantum.quantumcareer.domain.model

/**
 * Australian Quantum Computing Ecosystem Credits
 * Recognition and integration with key Australian quantum computing organizations
 */

/**
 * Q-CTRL Integration Information
 * Q-CTRL provides quantum firmware and infrastructure software
 */
data class QCtrlCredits(
    val companyName: String = "Q-CTRL",
    val description: String = "Quantum firmware and infrastructure software",
    val headquarters: String = "Sydney, Australia",
    val website: String = "https://q-ctrl.com",
    val integrationType: String = "Quantum Error Suppression",
    val features: List<String> = listOf(
        "Fire Opal quantum error suppression",
        "Boulder Opal quantum control optimization",
        "Open Controls open-source tools",
        "Black Opal quantum learning platform"
    ),
    val certificationLevel: String = "Enterprise Partner"
)

/**
 * LabScript Integration Information
 * Python-based quantum experiment control and automation
 */
data class LabScriptCredits(
    val projectName: String = "LabScript",
    val description: String = "Python-based quantum experiment control suite",
    val origin: String = "Monash University, Australia",
    val repository: String = "https://github.com/labscript-suite",
    val license: String = "BSD-2-Clause",
    val features: List<String> = listOf(
        "Hardware-timed experiment control",
        "Shot-based data acquisition",
        "Real-time analysis pipelines",
        "Multi-device synchronization"
    ),
    val version: String = "3.2.0"
)

/**
 * MicroQiskit Integration Information
 * Lightweight quantum computing framework for education
 */
data class MicroQiskitCredits(
    val projectName: String = "MicroQiskit",
    val description: String = "Minimal quantum computing framework",
    val maintainer: String = "IBM Research",
    val repository: String = "https://github.com/qiskit-community/MicroQiskit",
    val license: String = "Apache-2.0",
    val features: List<String> = listOf(
        "Lightweight quantum simulation",
        "Educational quantum circuits",
        "Cross-platform compatibility",
        "Minimal dependencies"
    ),
    val version: String = "0.6.0"
)

/**
 * Silicon Quantum Computing (SQC) Information
 * Australia's leading quantum computing company
 */
data class SQCCredits(
    val companyName: String = "Silicon Quantum Computing",
    val abbreviation: String = "SQC",
    val description: String = "Silicon-based quantum computing technology",
    val headquarters: String = "Sydney, Australia",
    val website: String = "https://sqc.com.au",
    val technology: String = "Atom-scale silicon quantum processors",
    val achievements: List<String> = listOf(
        "World's first integrated atom-scale quantum processor",
        "Atomic-precision manufacturing",
        "Record qubit coherence times",
        "Australian Quantum Computing Standards development"
    ),
    val standardsVersion: String = "5.2.0",
    val certificationPrograms: List<String> = listOf(
        "SQC Fidelity Certification",
        "Australian Quantum Standards Compliance",
        "Quantum Hardware Benchmarking"
    )
)

/**
 * Complete Australian Quantum Credits collection
 */
data class AustralianQuantumCredits(
    val qCtrl: QCtrlCredits = QCtrlCredits(),
    val labScript: LabScriptCredits = LabScriptCredits(),
    val microQiskit: MicroQiskitCredits = MicroQiskitCredits(),
    val sqc: SQCCredits = SQCCredits(),
    val acknowledgementsText: String = """
        QuantumCareer acknowledges and thanks the following Australian quantum
        computing organizations and projects for their contributions to the
        global quantum computing ecosystem:

        - Silicon Quantum Computing (SQC) for pioneering silicon-based quantum
          computing technology and establishing Australian Quantum Standards
        - Q-CTRL for quantum firmware and error suppression technologies
        - LabScript Suite (Monash University) for quantum experiment control tools
        - MicroQiskit community for accessible quantum computing education

        This integration supports Australian Standards v5.2.0 for quantum
        computing fidelity grading and certification.
    """.trimIndent(),
    val version: String = "5.2.0",
    val lastUpdated: String = "2026-01-29"
) {
    companion object {
        /**
         * Creates default Australian Quantum Credits instance
         */
        fun createDefault(): AustralianQuantumCredits = AustralianQuantumCredits()

        /**
         * Australian Standards version string
         */
        const val STANDARDS_VERSION = "5.2.0"

        /**
         * Integration version string
         */
        const val INTEGRATION_VERSION = "1.0.0"
    }

    /**
     * Returns a list of all credited organizations
     */
    val allOrganizations: List<String>
        get() = listOf(
            sqc.companyName,
            qCtrl.companyName,
            labScript.projectName,
            microQiskit.projectName
        )

    /**
     * Returns formatted credits string for display
     */
    fun getFormattedCredits(): String {
        return buildString {
            appendLine("=== Australian Quantum Computing Credits ===")
            appendLine()
            appendLine("Standards Version: $version")
            appendLine("Last Updated: $lastUpdated")
            appendLine()
            appendLine("--- ${sqc.companyName} (${sqc.abbreviation}) ---")
            appendLine("Description: ${sqc.description}")
            appendLine("Technology: ${sqc.technology}")
            appendLine("Website: ${sqc.website}")
            appendLine()
            appendLine("--- ${qCtrl.companyName} ---")
            appendLine("Description: ${qCtrl.description}")
            appendLine("Website: ${qCtrl.website}")
            appendLine()
            appendLine("--- ${labScript.projectName} ---")
            appendLine("Description: ${labScript.description}")
            appendLine("Origin: ${labScript.origin}")
            appendLine()
            appendLine("--- ${microQiskit.projectName} ---")
            appendLine("Description: ${microQiskit.description}")
            appendLine("Maintainer: ${microQiskit.maintainer}")
        }
    }
}
