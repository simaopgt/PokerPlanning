package com.idk.feature_poker_planning.di

import com.idk.feature_poker_planning.data.repository.DataStoreUserProfileRepositoryImpl
import com.idk.feature_poker_planning.data.repository.FirestoreRepositoryImpl
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
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
    abstract fun bindRoomRepository(
        impl: FirestoreRepositoryImpl
    ): RoomRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        impl: DataStoreUserProfileRepositoryImpl
    ): UserProfileRepository

}
