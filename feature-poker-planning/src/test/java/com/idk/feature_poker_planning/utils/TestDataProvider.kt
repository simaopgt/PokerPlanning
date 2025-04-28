package com.idk.feature_poker_planning.utils

import com.google.firebase.Timestamp
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.model.UserProfile

object TestDataProvider {
    const val VALID_AVATAR = "avatar_1"
    const val VALID_NAME = "User"
    const val INVALID_AVATAR = ""
    const val INVALID_NAME = ""
    const val HAS_PROFILE = true
    const val NO_PROFILE = false
    const val TEST_ROOM_ID = "room123"
    const val TEST_USER_ID = "user123"
    const val TEST_VOTE = 5
    const val SOME_USER_ID = "someId"

    val defaultUserProfile = UserProfile(
        userId = "", userName = VALID_NAME, avatar = VALID_AVATAR
    )

    val participantsMaps: List<Map<String, Any?>> = listOf(
        mapOf(
            FirestoreConstants.USER_ID_FIELD to "u1",
            FirestoreConstants.USER_NAME_FIELD to "Alice",
            FirestoreConstants.VOTE_FIELD to null
        ),
        mapOf(
            FirestoreConstants.USER_ID_FIELD to "u2",
            FirestoreConstants.USER_NAME_FIELD to "Bob",
            FirestoreConstants.VOTE_FIELD to 7L
        )
    )
    val participants: List<Participant> = participantsMaps.map { data ->
        Participant(
            userId = data[FirestoreConstants.USER_ID_FIELD] as String,
            name = data[FirestoreConstants.USER_NAME_FIELD] as String,
            vote = (data[FirestoreConstants.VOTE_FIELD] as? Long)?.toInt()
        )
    }

    val defaultRoom = Room(
        id = "room123", name = "TestRoom", createdAt = Timestamp.now()
    )

    val emptyNameRoom = Room(
        id = "room321", name = INVALID_NAME, createdAt = Timestamp.now()
    )

    val testError: RuntimeException = RuntimeException("Test error")
}
