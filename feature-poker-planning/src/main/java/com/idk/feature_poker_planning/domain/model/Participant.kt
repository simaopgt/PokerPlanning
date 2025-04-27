package com.idk.feature_poker_planning.domain.model

data class Participant(
    val userId: String,
    val name: String,
    val vote: Int? = null
)