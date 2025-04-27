package com.idk.feature_poker_planning.presentation.home

import com.idk.feature_poker_planning.domain.model.Room

data class HomeUiState(
    val rooms: List<Room> = emptyList(),
    val userName: String = "",
    val userAvatar: String = ""
)
