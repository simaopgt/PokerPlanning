package com.idk.feature_poker_planning.di

import com.idk.feature_poker_planning.data.repository.DataStoreUserProfileRepositoryImpl
import com.idk.feature_poker_planning.data.repository.FirestoreRepositoryImpl
import com.idk.feature_poker_planning.data.repository.VertexAiRepositoryImpl
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureModule {

    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(
        impl: FirestoreRepositoryImpl
    ): RoomRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        impl: DataStoreUserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindVertexAiRepository(
        impl: VertexAiRepositoryImpl
    ): VertexAiRepository

}
