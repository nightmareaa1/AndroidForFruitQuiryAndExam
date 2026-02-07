package com.example.userauth.di

import com.example.userauth.data.api.RatingApi
import com.example.userauth.data.api.CompetitionApi
import com.example.userauth.data.api.EvaluationModelApi
import com.example.userauth.data.api.EvaluationApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    
    @Provides
    @Singleton
    fun provideRatingApi(retrofit: Retrofit): RatingApi {
        return retrofit.create(RatingApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCompetitionApi(retrofit: Retrofit): CompetitionApi {
        return retrofit.create(CompetitionApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideEvaluationModelApi(retrofit: Retrofit): EvaluationModelApi {
        return retrofit.create(EvaluationModelApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideEvaluationApiService(retrofit: Retrofit): EvaluationApiService {
        return retrofit.create(EvaluationApiService::class.java)
    }
}
