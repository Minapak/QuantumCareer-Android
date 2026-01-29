# QuantumCareer Android Features

## Circuit Publishing

### DOI System
- Format: 10.5281/sqc.2026.XXXXX
- Permanent identifier
- Citation tracking

### Publishing Flow
1. Create/import circuit
2. Add metadata (title, description, category, tags)
3. Request peer review (optional)
4. Receive DOI upon approval

## Peer Review System

### Reviewer Levels
- **Junior**: New reviewers
- **Senior**: 10+ approved reviews
- **Expert**: 50+ approved reviews

### Review Process
1. Submit circuit for review
2. Auto-assign reviewers
3. Provide feedback
4. Approve/Revise/Reject

## Badge System

### Career Tiers
| Tier | Icon | Requirements | Benefits |
|------|------|--------------|----------|
| Bronze | 🥉 | First publication | Basic badge |
| Silver | 🥈 | 5 pubs + 10 cites | Peer review access |
| Gold | 🥇 | 20 pubs + 50 cites | Senior reviewer |
| Platinum | 💎 | 50 pubs + 200 cites | Expert + priority |

## SQC Fidelity Grading (v5.2.0)

### Australian Quantum Standards Integration
Full compliance with Standards for Quantum Computing (SQC) from the Australian Quantum Computing Standards Authority.

### FidelityGrade Tiers
| Grade | Icon | Fidelity Range | Description |
|-------|------|----------------|-------------|
| Platinum | 🏆 | 99.9%+ | Exceptional quantum fidelity |
| Gold | 🥇 | 99.0-99.9% | Excellent quantum fidelity |
| Silver | 🥈 | 95.0-99.0% | High quantum fidelity |
| Bronze | 🥉 | 90.0-95.0% | Good quantum fidelity |
| Standard | ✓ | 80.0-90.0% | Acceptable quantum fidelity |
| Developing | ○ | Below 80.0% | Fidelity improvement needed |

### SQC Components
- **SQCFidelityBadge.kt**: Composable component displaying fidelity grade with visual indicator
- **AustralianQuantumCredits.kt**: Model for tracking quantum credits earned under Australian standards
- **AustralianStandards.kt**: Model defining compliance rules and validation logic

### Badge Display
- SQC Fidelity badges appear in BadgesScreen alongside career badges
- Visual distinction between career achievements and fidelity grades
- Localized descriptions in all 5 supported languages (EN, KO, JA, ZH, DE)

## Citation Tracking

### Metrics
- **h-Index**: h papers with ≥h citations each
- **i10-Index**: Papers with ≥10 citations
- **Total Citations**: All-time citation count
- **Monthly Trend**: Citations per month chart

## Talent Features

### Search
- Filter by h-index, publications, badges
- Specialization filtering
- Location-based search

### Scouting
- Send scout offers
- View offer status
- Manage incoming offers

## Public Profile

- URL: https://quantum.career/profile/{username}
- Display name and bio
- Featured circuits
- Badges and stats
- Contact information (optional)

## Jobs Feature

### Jobs Screen
- Full job list with pagination
- Search by title, company, or skills
- Filter options:
  - Location type (Remote, Hybrid, On-site)
  - Employment type (Full-time, Part-time, Contract, Internship, Freelance)
  - Experience level (Entry, Mid, Senior, Lead, Executive)
  - Skills
- Tab navigation (Browse, Recommended, Saved, Applications)
- Quick save/unsave functionality

### Job Detail Screen
- Complete job information
- AI Match Score with breakdown (Skill, Experience, Certification match)
- Requirements and responsibilities
- Company information
- Benefits listing
- Apply button with application sheet
- Save/unsave functionality

### Job Application
- Cover letter toggle and editor
- Resume upload or URL paste
- Application info message
- Submission confirmation
- Application tracking in Applications tab

## Enhanced Profile Screen

### 5 Profile Tabs
1. **Overview**: Industry readiness score, contribution heatmap, streak info
2. **Certificates**: Earned certifications with LinkedIn share
3. **Badges**: Earned and in-progress badges
4. **Circuits**: Published circuits with stats
5. **Portfolio**: Skill breakdown visualization

### Contribution Heatmap
- GitHub-style 52-week grid
- 5 intensity levels
- Current and longest streak display

### Industry Readiness Score
- 0-100 score with level (Beginner/Intermediate/Advanced/Expert)
- Evidence breakdown by category

### Skill Breakdown
- 5 dimensions: Logic, Innovation, Contribution, Stability, Speed
- Percentage bars with colors

## Certification System

### Certification Test Flow
1. Test rules overview
2. Timed questions (50 questions)
3. Category-based scoring
4. Results with certificate

### Test Results
- Score display with pass/fail
- Performance by category breakdown
- Test statistics (time, accuracy)
- Certificate view (if passed)
- Take another test option

## Rankings System

### Leaderboard
- Global rankings
- Local (country-based) rankings
- Filtering by time period (All Time, This Week, Monthly, Quarterly, Yearly)
- User rank highlight

### My Rank Screen
- Current rank with change indicator
- Percentile display
- Tests completed
- Average score
- Institution and country rankings

## UI/UX Features

### Material Design 3
- Brand Color: Career-Gold (#FFB800)
- Dark mode as default
- Dynamic colors (Android 12+)
- Responsive layouts for Phone/Foldable/Tablet

### Localization (5 Languages)
- English (default)
- Korean (한국어)
- Japanese (日本語)
- Chinese (中文)
- German (Deutsch)
- **Instant Language Switching**: Uses AppCompatDelegate.setApplicationLocales() for immediate UI updates without restart
- **Language Selection Onboarding**: First-launch language selection screen
- **Graceful Offline Mode**: Returns default data when API is unavailable (no 404 errors shown)

### Navigation
- Bottom navigation bar
- Unified Navigation Drawer for ecosystem apps
- Deep links to SwiftQuantum, QuantumNative, Q-Bridge

### Settings Screen
- Account section (profile, logout)
- Notifications (push, email, job alerts, quiz reminders)
- Appearance (dark mode, language)
- Subscription management
- Integrations (LinkedIn)
- Support (help center, contact, bug report)
- Legal (privacy policy, terms)
- About (version info)

### Cross-App Authentication
- SharedAuthManager for single sign-on
- Guest mode with login access in Settings
- Account card in Profile with login/logout
