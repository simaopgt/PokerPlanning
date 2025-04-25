package com.idk.feature_poker_planning.domain.repository

import com.idk.feature_poker_planning.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun observeRooms(): Flow<List<Room>>
    suspend fun createRoom()
}
