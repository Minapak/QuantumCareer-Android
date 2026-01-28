# QuantumCareer Android Architecture

## Overview

QuantumCareer Android follows Clean Architecture with MVVM pattern.

## Layers

### Domain Layer
- Circuit models (PublishCircuitRequest, PublishedCircuit)
- Review models (PeerReview, ReviewerLevel)
- Badge models (CareerBadge, BadgeTier)
- Citation models (CitationStats)
- Talent models (TalentProfile, ScoutRequest, TalentOffer)
- Profile models (PublicProfile)

### Data Layer
- CareerPassportApi, PeerReviewApi, TalentApi
- Repository implementations
- DTO mappers

### Presentation Layer
- 8 ViewModels (Dashboard, Circuits, Publish, PeerReview, Badges, Citations, Talent, Profile)
- 8 Screens with Jetpack Compose
- Custom components (DOI Display, h-Index Chart, Badge Collection, Review Interface)

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
    │     └── InProgressBadges (progress bars)
    │
    ├── CircuitsTab
    │     └── PublishedCircuitCard
    │
    └── PortfolioTab
          └── SkillBreakdown (radar visualization)
```
