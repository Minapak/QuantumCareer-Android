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
