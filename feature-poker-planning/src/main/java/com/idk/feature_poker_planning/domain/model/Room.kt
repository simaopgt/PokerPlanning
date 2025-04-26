package com.idk.feature_poker_planning.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Room(
    val id: String = "",
    val name: String = "",
    val createdAt: Timestamp? = null
)
