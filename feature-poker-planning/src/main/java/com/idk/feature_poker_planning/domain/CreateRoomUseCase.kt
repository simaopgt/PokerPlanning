package com.idk.feature_poker_planning.domain

import com.google.firebase.Timestamp
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import java.util.UUID
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(desiredName: String? = null) {
        val roomId = UUID.randomUUID().toString()
        val roomName = desiredName.takeUnless { it.isNullOrBlank() } ?: ""

        val room = Room(
            id = roomId, name = roomName, createdAt = Timestamp.now()
        )

        repository.createRoom(room)
    }
}
