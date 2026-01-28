package com.swiftquantum.quantumcareer.data.mapper

import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.domain.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
private val dateFormatter = DateTimeFormatter.ISO_DATE

private fun String.toLocalDateTime(): LocalDateTime {
    return try {
        LocalDateTime.parse(this, dateTimeFormatter)
    } catch (e: DateTimeParseException) {
        try {
            LocalDateTime.parse(this.replace("Z", ""), dateTimeFormatter)
        } catch (e: DateTimeParseException) {
            LocalDateTime.now()
        }
    }
}

private fun String.toLocalDate(): LocalDate {
    return try {
        LocalDate.parse(this, dateFormatter)
    } catch (e: DateTimeParseException) {
        try {
            LocalDateTime.parse(this, dateTimeFormatter).toLocalDate()
        } catch (e: DateTimeParseException) {
            LocalDate.now()
        }
    }
}

// Circuit mappers
fun PublishedCircuitDto.toDomain(): PublishedCircuit = PublishedCircuit(
    id = id,
    doi = doi,
    title = title,
    description = description,
    qasmCode = qasmCode,
    tags = tags,
    authorId = authorId,
    authorName = authorName,
    status = CircuitStatus.fromString(status),
    citationCount = citationCount,
    publishedAt = publishedAt?.toLocalDateTime(),
    createdAt = createdAt.toLocalDateTime(),
    updatedAt = updatedAt.toLocalDateTime()
)

fun PublishCircuitRequest.toDto(): PublishCircuitRequestDto = PublishCircuitRequestDto(
    title = title,
    description = description,
    qasmCode = qasmCode,
    tags = tags,
    isPublic = isPublic
)

fun CiteCircuitRequest.toDto(): CiteCircuitRequestDto = CiteCircuitRequestDto(
    citingCircuitId = citingCircuitId,
    citationContext = citationContext
)

// Review mappers
fun PeerReviewDto.toDomain(): PeerReview = PeerReview(
    id = id,
    circuitId = circuitId,
    circuitTitle = circuitTitle,
    circuitAuthor = circuitAuthor,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    reviewerLevel = ReviewerLevel.fromString(reviewerLevel),
    status = ReviewStatus.fromString(status),
    decision = ReviewDecision.fromString(decision),
    comment = comment,
    submittedAt = submittedAt?.toLocalDateTime(),
    createdAt = createdAt.toLocalDateTime(),
    qasmCode = qasmCode
)

fun ReviewerStatsDto.toDomain(): ReviewerStats = ReviewerStats(
    totalReviews = totalReviews,
    approvedCount = approvedCount,
    rejectedCount = rejectedCount,
    reviewerLevel = ReviewerLevel.fromString(reviewerLevel) ?: ReviewerLevel.JUNIOR,
    reviewsUntilNextLevel = reviewsUntilNextLevel
)

// Badge mappers
fun CareerBadgeDto.toDomain(): CareerBadge = CareerBadge(
    id = id,
    tier = BadgeTier.fromString(tier),
    name = name,
    description = description,
    earned = earned,
    earnedAt = earnedAt?.toLocalDateTime(),
    progress = progress?.toDomain()
)

fun BadgeProgressDto.toDomain(): BadgeProgress = BadgeProgress(
    publicationsRequired = publicationsRequired,
    publicationsCurrent = publicationsCurrent,
    citationsRequired = citationsRequired,
    citationsCurrent = citationsCurrent,
    percentage = percentage
)

fun BadgeListResponseDto.toDomain(): BadgeCollection = BadgeCollection(
    badges = badges.map { it.toDomain() },
    nextBadge = nextBadge?.toDomain(),
    currentTier = BadgeTier.fromString(currentTier)
)

// Citation mappers
fun CitationStatsDto.toDomain(): CitationStats = CitationStats(
    totalCitations = totalCitations,
    hIndex = hIndex,
    i10Index = i10Index,
    totalPublications = totalPublications,
    citationHistory = citationHistory.map { it.toDomain() },
    topCitedCircuits = topCitedCircuits.map { it.toDomain() }
)

fun CitationHistoryPointDto.toDomain(): CitationHistoryPoint = CitationHistoryPoint(
    date = date.toLocalDate(),
    cumulativeCitations = cumulativeCitations,
    newCitations = newCitations
)

fun TopCitedCircuitDto.toDomain(): TopCitedCircuit = TopCitedCircuit(
    circuitId = circuitId,
    title = title,
    doi = doi,
    citationCount = citationCount
)

fun CitationDetailDto.toDomain(): CitationDetail = CitationDetail(
    id = id,
    citingCircuitId = citingCircuitId,
    citingCircuitTitle = citingCircuitTitle,
    citingAuthor = citingAuthor,
    citedCircuitId = citedCircuitId,
    citationContext = citationContext,
    createdAt = createdAt.toLocalDateTime()
)

// Talent mappers
fun TalentProfileDto.toDomain(): TalentProfile = TalentProfile(
    userId = userId,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    bio = bio,
    institution = institution,
    specializations = specializations,
    hIndex = hIndex,
    i10Index = i10Index,
    totalPublications = totalPublications,
    totalCitations = totalCitations,
    badgeTier = BadgeTier.fromString(badgeTier),
    isAvailable = isAvailable,
    profileUrl = profileUrl
)

fun TalentSearchResponseDto.toDomain(): TalentSearchResult = TalentSearchResult(
    profiles = profiles.map { it.toDomain() },
    total = total,
    page = page,
    perPage = perPage
)

fun TalentOfferDto.toDomain(): TalentOffer = TalentOffer(
    id = id,
    fromUserId = fromUserId,
    fromUserName = fromUserName,
    fromOrganization = fromOrganization,
    offerType = OfferType.fromString(offerType),
    position = position,
    message = message,
    details = details,
    status = OfferStatus.fromString(status),
    createdAt = createdAt.toLocalDateTime(),
    respondedAt = respondedAt?.toLocalDateTime()
)

// Profile mappers
fun PublicProfileDto.toDomain(): PublicProfile = PublicProfile(
    userId = userId,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    bio = bio,
    institution = institution,
    location = location,
    website = website,
    specializations = specializations,
    hIndex = hIndex,
    i10Index = i10Index,
    totalPublications = totalPublications,
    totalCitations = totalCitations,
    badgeTier = BadgeTier.fromString(badgeTier),
    badges = badges.map { it.toDomain() },
    recentCircuits = recentCircuits.map { it.toDomain() },
    isPublic = isPublic,
    profileUrl = profileUrl,
    joinedAt = joinedAt.toLocalDateTime()
)

fun UpdateProfileRequest.toDto(): UpdateProfileRequestDto = UpdateProfileRequestDto(
    displayName = displayName,
    bio = bio,
    institution = institution,
    location = location,
    website = website,
    specializations = specializations,
    isPublic = isPublic,
    isAvailableForHire = isAvailableForHire
)

fun DashboardStatsDto.toDomain(): DashboardStats = DashboardStats(
    totalPublications = totalPublications,
    totalCitations = totalCitations,
    hIndex = hIndex,
    i10Index = i10Index,
    pendingReviews = pendingReviews,
    currentBadgeTier = BadgeTier.fromString(currentBadgeTier),
    nextBadgeProgress = nextBadgeProgress,
    recentActivity = recentActivity.map { it.toDomain() }
)

fun ActivityItemDto.toDomain(): ActivityItem = ActivityItem(
    id = id,
    type = ActivityType.fromString(type),
    title = title,
    description = description,
    createdAt = createdAt.toLocalDateTime()
)
