package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.RoomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinRoomUseCase @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(roomId: String) {
        val profile = getUserProfileUseCase()
        roomRepository.addParticipant(
            roomId = roomId,
            userId = profile.userId,
            name = profile.userName,
            avatar = profile.avatar
        )
    }
}
