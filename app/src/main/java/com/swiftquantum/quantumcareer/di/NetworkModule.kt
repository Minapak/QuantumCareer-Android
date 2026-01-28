package com.swiftquantum.quantumcareer.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.swiftquantum.quantumcareer.BuildConfig
import com.swiftquantum.quantumcareer.data.api.CareerPassportApi
import com.swiftquantum.quantumcareer.data.api.CertificateApi
import com.swiftquantum.quantumcareer.data.api.JobsApi
import com.swiftquantum.quantumcareer.data.api.PeerReviewApi
import com.swiftquantum.quantumcareer.data.api.QuizApi
import com.swiftquantum.quantumcareer.data.api.RankingsApi
import com.swiftquantum.quantumcareer.data.api.TalentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCareerPassportApi(retrofit: Retrofit): CareerPassportApi {
        return retrofit.create(CareerPassportApi::class.java)
    }

    @Provides
    @Singleton
    fun providePeerReviewApi(retrofit: Retrofit): PeerReviewApi {
        return retrofit.create(PeerReviewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTalentApi(retrofit: Retrofit): TalentApi {
        return retrofit.create(TalentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizApi(retrofit: Retrofit): QuizApi {
        return retrofit.create(QuizApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCertificateApi(retrofit: Retrofit): CertificateApi {
        return retrofit.create(CertificateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRankingsApi(retrofit: Retrofit): RankingsApi {
        return retrofit.create(RankingsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideJobsApi(retrofit: Retrofit): JobsApi {
        return retrofit.create(JobsApi::class.java)
    }
}
