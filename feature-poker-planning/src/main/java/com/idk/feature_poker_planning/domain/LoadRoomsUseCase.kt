package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRoomsUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    operator fun invoke(): Flow<List<Room>> = repository.observeRooms()
}
