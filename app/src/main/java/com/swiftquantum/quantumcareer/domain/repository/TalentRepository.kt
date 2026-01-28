package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface TalentRepository {
    suspend fun searchTalent(criteria: TalentSearchCriteria): Result<TalentSearchResult>
    suspend fun getTalentProfile(userId: String): Result<TalentProfile>
    suspend fun scoutTalent(request: ScoutRequest): Result<Boolean>
    suspend fun getOffers(type: OfferType? = null, status: OfferStatus? = null): Result<List<TalentOffer>>
    suspend fun getSentOffers(): Result<List<TalentOffer>>
    suspend fun respondToOffer(offerId: String, accept: Boolean, message: String?): Result<Boolean>
    suspend fun withdrawOffer(offerId: String): Result<Boolean>
}
