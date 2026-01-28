# Changelog

## [1.1.0] - 2026-01-28

### Added
- **Jobs Screen**: Full job search with filters (location type, employment type, experience level, skills)
- **Job Detail Screen**: Comprehensive job information with AI match score, requirements, and application flow
- **Job Application**: Cover letter, resume upload, and application tracking
- **Enhanced Profile Screen**: 5 tabs (Overview, Certificates, Badges, Circuits, Portfolio)
- **Contribution Heatmap**: GitHub-style contribution timeline
- **Skill Breakdown**: Visual skill radar with logic, innovation, contribution, stability, speed
- **Industry Readiness Score**: Calculated score with evidence breakdown
- **Settings Screen**: Full configuration including account, notifications, language, theme, integrations
- **Certification Test Flow**: Timed quizzes with tier progression
- **Test Results Screen**: Score breakdown, performance by category, certificate issuance
- **Rankings Screen**: Global and local leaderboards with filtering
- **Unified Navigation Drawer**: Deep link integration with SwiftQuantum Ecosystem
- **SharedAuthManager**: Cross-app authentication for single sign-on
- **5-Language Localization**: Added Japanese (日本語), Chinese (中文), German (Deutsch)
- **Lottie Animations**: Cloud feedback and loading states
- **Responsive Layouts**: Support for Phone, Foldable, and Tablet devices
- **Guest Mode**: Browse without login, access login from Settings

### Changed
- Enhanced Material Design 3 with brand color (Career-Gold #FFB800)
- Improved dark mode as default theme
- Updated navigation system with drawer integration

### Fixed
- ProfileScreen Circuit/Badge model imports
- RankingModels smart cast issue
- JobFilterBottomSheet experimental API annotation
- Multiple string resource duplicates and missing entries

## [1.0.0] - 2026-01-28

### Added
- Initial release of QuantumCareer Android
- Complete iOS app feature parity
- DOI-based circuit publishing
- Peer review system (Junior/Senior/Expert)
- Badge tiers (Bronze/Silver/Gold/Platinum)
- Citation tracking (h-Index, i10-Index)
- Talent search and scouting
- Public profile support
- Deep link support (https://quantum.career/profile/{username})
- Material Design 3 UI
- Clean Architecture with MVVM
