# QuantumCareer Android Architecture

## Overview

QuantumCareer Android follows Clean Architecture with MVVM pattern.

## Layers

### Domain Layer
- Circuit models (PublishCircuitRequest, PublishedCircuit)
- Review models (PeerReview, ReviewerLevel)
- Badge models (CareerBadge, BadgeTier, FidelityGrade)
- Citation models (CitationStats)
- Talent models (TalentProfile, ScoutRequest, TalentOffer)
- Profile models (PublicProfile)
- Australian Standards models (AustralianQuantumCredits, AustralianStandards)

### Data Layer
- CareerPassportApi, PeerReviewApi, TalentApi
- Repository implementations
- DTO mappers

### Presentation Layer
- 8 ViewModels (Dashboard, Circuits, Publish, PeerReview, Badges, Citations, Talent, Profile)
- 8 Screens with Jetpack Compose
- Custom components (DOI Display, h-Index Chart, Badge Collection, Review Interface, SQCFidelityBadge)

## Deep Linking

```
https://quantum.career/profile/{username}
      │
      ▼
AndroidManifest.xml (intent-filter)
      │
      ▼
MainActivity → ProfileScreen
```

## Cross-App Integration

### SharedAuthManager
Cross-app authentication via ContentProvider with signature-level permissions:

```kotlin
class SharedAuthManager(context: Context) {
    fun observeAuthState(): Flow<AuthData>
    suspend fun saveAuth(authData: AuthData)
    suspend fun clearAuth()
}
```

### Unified Navigation Drawer
Deep linking between SwiftQuantum Ecosystem apps

### New Screens (v1.1.0)
- **JobsScreen**: Job search with filters and pagination
- **JobDetailScreen**: Job information with AI match score
- **JobApplicationSheet**: Cover letter and resume submission
- **Enhanced ProfileScreen**: 5 tabs with contribution heatmap
- **SettingsScreen**: Full configuration
- **CertificationTestScreen**: Timed quiz flow
- **TestResultScreen**: Score breakdown and certificate
- **RankingsScreen**: Global and local leaderboards

## Jobs Feature Architecture

```
JobsScreen
    │
    ├── JobFilterBottomSheet (filter options)
    │
    └── JobCard → JobDetailScreen
                      │
                      └── JobApplicationSheet
                              │
                              └── ApplicationConfirmation
```

## Profile Tabs Architecture

```
ProfileScreen
    │
    ├── OverviewTab
    │     ├── IndustryReadinessScore
    │     └── ContributionHeatmap
    │
    ├── CertificatesTab
    │     └── CertificateCard (share to LinkedIn)
    │
    ├── BadgesTab
    │     ├── EarnedBadges (horizontal scroll)
    │     ├── SQCFidelityBadge (Australian Standards grade)
    │     └── InProgressBadges (progress bars)
    │
    ├── CircuitsTab
    │     └── PublishedCircuitCard
    │
    └── PortfolioTab
          └── SkillBreakdown (radar visualization)
```

## SQC Fidelity Architecture (v5.2.0)

### Australian Quantum Standards Integration

```
AustralianStandards.kt
    │
    ├── FidelityGrade (enum)
    │     ├── Platinum (99.9%+)
    │     ├── Gold (99.0-99.9%)
    │     ├── Silver (95.0-99.0%)
    │     ├── Bronze (90.0-95.0%)
    │     ├── Standard (80.0-90.0%)
    │     └── Developing (<80.0%)
    │
    └── AustralianQuantumCredits.kt
          └── Credit validation and tracking

SQCFidelityBadge.kt (Composable)
    │
    ├── Grade icon display
    ├── Fidelity percentage
    └── Localized grade description (5 languages)
```

### Component Integration
- BadgesScreen displays SQCFidelityBadge alongside career badges
- Localization strings in: strings.xml (EN), strings-ko.xml, strings-ja.xml, strings-zh.xml, strings-de.xml
