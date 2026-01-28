package com.swiftquantum.quantumcareer.di

import com.swiftquantum.quantumcareer.data.repository.CareerRepositoryImpl
import com.swiftquantum.quantumcareer.data.repository.CertificateRepositoryImpl
import com.swiftquantum.quantumcareer.data.repository.JobsRepositoryImpl
import com.swiftquantum.quantumcareer.data.repository.QuizRepositoryImpl
import com.swiftquantum.quantumcareer.data.repository.RankingsRepositoryImpl
import com.swiftquantum.quantumcareer.data.repository.ReviewRepositoryImpl
import com.swiftquantum.quantumcareer.data.repository.TalentRepositoryImpl
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import com.swiftquantum.quantumcareer.domain.repository.CertificateRepository
import com.swiftquantum.quantumcareer.domain.repository.JobsRepository
import com.swiftquantum.quantumcareer.domain.repository.QuizRepository
import com.swiftquantum.quantumcareer.domain.repository.RankingsRepository
import com.swiftquantum.quantumcareer.domain.repository.ReviewRepository
import com.swiftquantum.quantumcareer.domain.repository.TalentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCareerRepository(
        careerRepositoryImpl: CareerRepositoryImpl
    ): CareerRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindTalentRepository(
        talentRepositoryImpl: TalentRepositoryImpl
    ): TalentRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): QuizRepository

    @Binds
    @Singleton
    abstract fun bindCertificateRepository(
        certificateRepositoryImpl: CertificateRepositoryImpl
    ): CertificateRepository

    @Binds
    @Singleton
    abstract fun bindRankingsRepository(
        rankingsRepositoryImpl: RankingsRepositoryImpl
    ): RankingsRepository

    @Binds
    @Singleton
    abstract fun bindJobsRepository(
        jobsRepositoryImpl: JobsRepositoryImpl
    ): JobsRepository
}
