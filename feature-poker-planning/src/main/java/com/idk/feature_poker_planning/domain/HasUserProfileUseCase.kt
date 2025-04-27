package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HasUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(): Boolean =
        repository.hasProfile()
}
