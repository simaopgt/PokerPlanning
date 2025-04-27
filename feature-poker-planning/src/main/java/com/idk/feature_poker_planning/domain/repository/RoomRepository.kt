package com.idk.feature_poker_planning.domain.repository

import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun observeRooms(): Flow<List<Room>>
    suspend fun createRoom(room: Room)
    fun observeParticipants(roomId: String): Flow<List<Participant>>
    suspend fun submitVote(roomId: String, userId: String, vote: Int)
}
