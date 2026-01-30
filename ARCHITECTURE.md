# QuantumCareer Android Architecture

**Version 5.6.0** | **2026-01-31**

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
- Skill Verification models (SkillAssessment, CompetencyLevel, VerificationResult)
- Job Matching models (MatchScore, CareerPath, RecommendationResult)

### Data Layer
- CareerPassportApi, PeerReviewApi, TalentApi
- Repository implementations
- DTO mappers
- CacheManager (3-Layer Cache)
- RedisClient integration
- SentryErrorHandler

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

## 3-Layer Cache Architecture (v5.6.0)

### Cache Layers
```
Request
    │
    ▼
┌─────────────────────────────────────────────────┐
│  L1: Memory Cache (LRU)                         │
│  - Hot data with TTL                            │
│  - Max 100MB heap allocation                    │
│  - Sub-millisecond access                       │
└─────────────────────────────────────────────────┘
    │ Cache Miss
    ▼
┌─────────────────────────────────────────────────┐
│  L2: Disk Cache (Room + DataStore)              │
│  - Persistent storage                           │
│  - Max 500MB disk allocation                    │
│  - Offline support                              │
└─────────────────────────────────────────────────┘
    │ Cache Miss
    ▼
┌─────────────────────────────────────────────────┐
│  L3: Redis Network Cache                        │
│  - Distributed caching                          │
│  - Session management                           │
│  - Real-time synchronization                    │
└─────────────────────────────────────────────────┘
    │ Cache Miss
    ▼
  API Request
```

### CacheManager Implementation
```kotlin
class CacheManager @Inject constructor(
    private val memoryCache: MemoryCache,
    private val diskCache: DiskCache,
    private val redisClient: RedisClient
) {
    suspend fun <T> get(key: String): T?
    suspend fun <T> put(key: String, value: T, ttl: Duration)
    suspend fun invalidate(key: String)
    suspend fun invalidateAll()
}
```

## Redis Integration (v5.6.0)

### Redis Client Architecture
```
RedisClient
    │
    ├── ConnectionPool (max 10 connections)
    │
    ├── Operations
    │     ├── get/set/delete
    │     ├── publish/subscribe
    │     └── transaction support
    │
    └── Fallback
          └── Graceful degradation to L2 cache
```

### Use Cases
- User session management
- Real-time job matching updates
- Cross-device synchronization
- Cache invalidation broadcasts

## Sentry Error Monitoring (v5.6.0)

### Integration Architecture
```
SentryErrorHandler
    │
    ├── Crash Reporting
    │     ├── Automatic exception capture
    │     ├── Stack trace symbolication
    │     └── Device/OS context
    │
    ├── Performance Monitoring
    │     ├── Transaction tracing
    │     ├── API call duration
    │     └── Screen load times
    │
    └── User Feedback
          ├── Session replay
          └── Breadcrumb trails
```

### Configuration
```kotlin
SentryAndroid.init(context) { options ->
    options.dsn = BuildConfig.SENTRY_DSN
    options.environment = BuildConfig.BUILD_TYPE
    options.tracesSampleRate = 1.0
    options.isEnableAutoSessionTracking = true
}
```

## Operations Readiness (v5.6.0)

### Checklist Architecture
```
OperationsReadinessChecker
    │
    ├── Infrastructure Checks
    │     ├── API endpoint health
    │     ├── Redis connectivity
    │     └── CDN availability
    │
    ├── Service Checks
    │     ├── Authentication service
    │     ├── Job matching service
    │     └── Notification service
    │
    └── Configuration Checks
          ├── Feature flags
          ├── Remote config sync
          └── Version compatibility
```

## Skill Verification & Job Matching (v5.6.0)

### Skill Verification Flow
```
SkillVerificationManager
    │
    ├── Assessment Engine
    │     ├── AI-powered skill analysis
    │     ├── Certification validation
    │     └── Experience verification
    │
    └── Competency Mapping
          ├── Industry standards alignment
          ├── Role-specific requirements
          └── Growth path identification
```

### Enhanced Job Matching
```
JobMatchingEngine
    │
    ├── Multi-factor Analysis
    │     ├── Skill match (40%)
    │     ├── Experience match (30%)
    │     ├── Certification match (20%)
    │     └── Culture fit (10%)
    │
    ├── Real-time Updates
    │     ├── Redis pub/sub for new jobs
    │     └── Push notifications
    │
    └── Career Path Suggestions
          ├── Skill gap analysis
          └── Learning recommendations
```
