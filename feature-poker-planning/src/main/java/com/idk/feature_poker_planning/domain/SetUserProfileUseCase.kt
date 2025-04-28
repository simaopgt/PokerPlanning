package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.UserProfile
import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        val actualId = profile.userId.takeIf { it.isNotBlank() }
            ?: java.util.UUID.randomUUID().toString()
        val toSave = profile.copy(userId = actualId)
        repository.saveProfile(toSave)
    }
}
