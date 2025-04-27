package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadParticipantsUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    operator fun invoke(roomId: String): Flow<List<Participant>> =
        repository.observeParticipants(roomId)
}
