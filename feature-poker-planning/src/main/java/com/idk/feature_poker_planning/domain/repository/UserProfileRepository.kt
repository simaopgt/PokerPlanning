package com.idk.feature_poker_planning.domain.repository

import com.idk.feature_poker_planning.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun saveProfile(profile: UserProfile)
    suspend fun hasProfile(): Boolean
}