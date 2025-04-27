package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.RoomRepository
import javax.inject.Inject

class SubmitVoteUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String, userId: String, vote: Int) {
        repository.submitVote(roomId, userId, vote)
    }
}
