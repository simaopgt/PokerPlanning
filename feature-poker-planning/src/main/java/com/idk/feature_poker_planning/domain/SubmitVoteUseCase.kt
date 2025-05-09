package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.RoomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitVoteUseCase @Inject constructor(
    private val repository: RoomRepository, private val getUserProfileUseCase: GetUserProfileUseCase
) {
    suspend operator fun invoke(roomId: String, vote: Int) {
        val profile = getUserProfileUseCase()
        repository.submitVote(
            roomId = roomId,
            userId = profile.userId,
            name = profile.userName,
            avatar = profile.avatar,
            vote = vote
        )
    }
}
