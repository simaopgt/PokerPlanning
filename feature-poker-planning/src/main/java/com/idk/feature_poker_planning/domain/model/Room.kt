package com.idk.feature_poker_planning.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Room(
    var id: String = "",
    var name: String = "",
    var createdAt: Timestamp = Timestamp.now(),
    var sessionCount: Int = 0,
    var participants: List<Participant> = emptyList(),
)
