package com.idk.feature_poker_planning.utils

import com.google.firebase.Timestamp
import com.idk.feature_poker_planning.domain.model.Room

object TestDataProvider {
    val defaultRoom: Room = Room(
        id = "room123", name = "TestRoom", createdAt = Timestamp.now()
    )

    val emptyNameRoom: Room = Room(
        id = "room321", name = "", createdAt = Timestamp.now()
    )

    val testError: RuntimeException = RuntimeException("Test error")
}
