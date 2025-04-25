package com.idk.feature_poker_planning.di

import com.idk.feature_poker_planning.data.repository.FirestoreRoomRepository
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureModule {
    @Binds
    @Singleton
    abstract fun bindRoomRepository(
        impl: FirestoreRoomRepository
    ): RoomRepository
}
