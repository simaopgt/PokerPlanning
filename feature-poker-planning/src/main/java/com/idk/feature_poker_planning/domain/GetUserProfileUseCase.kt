package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.UserProfile
import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(): UserProfile = repository.getProfile()
}