# QuantumCareer Android

Quantum Computing Career Portfolio Platform for Android

**Version 5.8.0** | **2026-01-31**

## Overview

QuantumCareer Android is the Android port of the iOS QuantumCareer app, providing career development tools for quantum computing professionals with DOI-based publishing, peer review, and talent matching. Part of the SwiftQuantum Ecosystem.

## Features

- **DOI Publishing**: Circuit publishing with DOI (10.5281/sqc.2026.XXXXX)
- **Peer Review**: Junior/Senior/Expert reviewer system
- **Badge Tiers**: Bronze, Silver, Gold, Platinum achievements
- **SQC Fidelity Grading**: Australian Quantum Standards compliance (Platinum/Gold/Silver/Bronze/Standard/Developing)
- **Citation Tracking**: h-Index and i10-Index metrics
- **Talent Matching**: Search and scout quantum talent
- **Public Profile**: Shareable career profiles
- **Jobs Feature**: Full job search, filtering, and application system
- **Enhanced Profile**: Contribution heatmap, skill breakdown, portfolio tabs
- **Settings Screen**: Full configuration with account, notifications, language
- **Admin Panel**: Complete admin dashboard for platform management
  - Dashboard with system stats and health monitoring
  - User management (search, premium grants, bans)
  - Content management (questions, jobs, badges)
  - System settings and security configuration
- **Onboarding Screen**: Animated introduction with 5-page pager
- **Certification Tests**: Timed quizzes with multiple tiers
- **Rankings System**: Global and local leaderboards
- **5 Languages**: EN, KO, JA, ZH, DE
- **Unified Navigation Drawer**: Deep link integration with ecosystem apps
- **Cross-App Authentication**: Single sign-on across all SwiftQuantum apps
- **Operations Readiness Checklist**: Production deployment readiness verification
- **3-Layer Cache Architecture**: Memory, Disk, and Network caching strategy
- **Redis Integration**: High-performance distributed caching support
- **Sentry Error Monitoring**: Real-time error tracking and crash reporting
- **Skill Verification & Job Matching**: Enhanced AI-powered skill assessment and job recommendations

## Requirements

- Android 8.0 (API 26) or higher
- Android Studio Ladybug or newer
- Kotlin 2.0+
- JDK 17+

## Tech Stack

- **Language**: Kotlin 2.0
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt
- **Network**: Retrofit + OkHttp
- **Caching**: 3-Layer Cache (Memory/Disk/Redis)
- **Monitoring**: Sentry Error Tracking
- **Analytics**: Firebase Analytics

## Badge Requirements

### Career Badges
| Tier | Requirements |
|------|--------------|
| Bronze | First circuit published |
| Silver | 5 publications + 10 citations |
| Gold | 20 publications + 50 citations |
| Platinum | 50 publications + 200 citations |

### SQC Fidelity Grades (Australian Standards)
| Grade | Fidelity Range |
|-------|----------------|
| Platinum | 99.9%+ |
| Gold | 99.0-99.9% |
| Silver | 95.0-99.0% |
| Bronze | 90.0-95.0% |
| Standard | 80.0-90.0% |
| Developing | Below 80.0% |

## Build & Run

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device/emulator

## License

Copyright © 2026 SwiftQuantum. All rights reserved.
